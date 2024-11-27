package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.dto.WalletDTO;
import com.postfinance.cryptowallet.dto.WalletPerformanceDTO;
import com.postfinance.cryptowallet.mapper.WalletMapper;
import com.postfinance.cryptowallet.model.Asset;
import com.postfinance.cryptowallet.model.Performance;
import com.postfinance.cryptowallet.model.PerformanceResult;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.repository.AssetRepository;
import com.postfinance.cryptowallet.repository.PerformanceRepository;
import com.postfinance.cryptowallet.repository.WalletRepository;
import com.postfinance.cryptowallet.util.AssetCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final AssetRepository assetRepository;
    private final PerformanceRepository performanceRepository;
    private final WalletRepository walletRepository;
    private final CoincapService coincapService;
    private final WalletHistoryService walletHistoryService;
    private final WalletMapper walletMapper;
    private final AssetCache assetCache;
    private static final String WALLET_NOT_FOUND = "Wallet not found";

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public WalletDTO createWallet(Wallet wallet) {
        for (Asset asset : wallet.getAssets()) {
            String assetId = assetCache.getIdBySymbol(asset.getSymbol());
            if (assetId == null) {
                throw new RuntimeException("Asset not found for symbol: " + asset.getSymbol());
            }
            asset.setExternalId(assetId);
            if (asset.getPrice() == null) {
                Double latestPrice = coincapService.getLatestPrice(asset.getExternalId());
                asset.setInitialPrice(latestPrice != null ? BigDecimal.valueOf(latestPrice) : BigDecimal.ZERO);
                asset.setPrice(asset.getInitialPrice());
            }
        }
        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toWalletDTO(savedWallet);
    }

    public WalletDTO getWalletDetails(Long walletId) {
        return walletMapper.toWalletDTO(walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException(WALLET_NOT_FOUND)));
    }

    public void deleteWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException(WALLET_NOT_FOUND));

        for (Asset asset : wallet.getAssets()) {
            assetRepository.delete(asset);
        }
        walletRepository.delete(wallet);
    }

    public List<Wallet> findAllWallets() {
        return walletRepository.findAll();
    }

    //@Async
    @Async
    @Transactional
    public void updateWalletData(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException(WALLET_NOT_FOUND));

        List<Asset> assets = wallet.getAssets();
        updateAssetPricesConcurrently(assets);

        WalletPerformanceDTO walletPerformanceDTO = calculateAndSaveWalletMetrics(walletId);
        log.info("Successfully updated wallet data for wallet ID: {}, walletPerformanceDTO: {}.", walletId, walletPerformanceDTO);
    }

    public void updateAssetPricesConcurrently(List<Asset> assets) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Asset asset : assets) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                log.info("Submitting request for asset {} at {}", asset.getSymbol(), LocalDateTime.now());
                try {
                    log.info("Fetching price for asset {}...", asset.getSymbol());
                    Double latestPrice = coincapService.getLatestPrice(asset.getExternalId());
                    if (latestPrice != null) {
                        asset.setPrice(BigDecimal.valueOf(latestPrice));
                        assetRepository.save(asset);

                        Performance performance = calculatePerformance(asset);
                        performanceRepository.save(performance);

                        log.info("Updated price and performance for asset {}: price={}, performance={}",
                                asset.getSymbol(), latestPrice, performance.getPerformancePercentage());
                    }
                } catch (Exception e) {
                    log.error("Error updating price for asset {}: {}", asset.getSymbol(), e.getMessage());
                }
            }, executorService);

            futures.add(future);

            if (futures.size() == 3) {
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                futures.clear();
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }



    public WalletPerformanceDTO calculateAndSaveWalletMetrics(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException(WALLET_NOT_FOUND));

        BigDecimal totalValue = calculateTotalValue(wallet);
        PerformanceResult performanceResult = calculateAssetPerformance(wallet.getAssets());

        wallet.setTotalValue(totalValue);
        wallet.setBestAsset(performanceResult.bestAsset());
        wallet.setBestPerformance(performanceResult.bestPerformance());
        wallet.setWorstAsset(performanceResult.worstAsset());
        wallet.setWorstPerformance(performanceResult.worstPerformance());

        walletRepository.save(wallet);
        walletHistoryService.saveWalletHistory(walletId, totalValue);

        return new WalletPerformanceDTO(
                totalValue.setScale(2, RoundingMode.HALF_UP),
                performanceResult.bestAsset() != null ? performanceResult.bestAsset().getSymbol() : null,
                performanceResult.bestPerformancePercentage() > 0
                        ? BigDecimal.valueOf(performanceResult.bestPerformancePercentage()).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO,
                performanceResult.worstAsset() != null ? performanceResult.worstAsset().getSymbol() : null,
                performanceResult.worstPerformancePercentage() > 0
                        ? BigDecimal.valueOf(performanceResult.worstPerformancePercentage()).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO
        );
    }

    private Performance calculatePerformance(Asset asset) {
        Performance performance = new Performance();
        performance.setAssetId(asset.getId());
        performance.setTimestamp(LocalDateTime.now(ZoneId.systemDefault()));

        BigDecimal initialPrice = asset.getInitialPrice();
        BigDecimal latestPrice = asset.getPrice();

        if (initialPrice != null && latestPrice != null && initialPrice.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal percentageChange = latestPrice.subtract(initialPrice)
                    .divide(initialPrice, 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            performance.setPerformancePercentage(percentageChange.doubleValue());
        } else {
            performance.setPerformancePercentage(0.0);
        }

        return performance;
    }


    private BigDecimal calculateTotalValue(Wallet wallet) {
        return wallet.getAssets().stream()
                .map(asset -> asset.getQuantity().multiply(asset.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PerformanceResult calculateAssetPerformance(List<Asset> assets) {
        Asset bestAsset = null;
        Asset worstAsset = null;
        Performance bestPerformance = null;
        Performance worstPerformance = null;

        double bestPerformancePercentage = Double.NEGATIVE_INFINITY;
        double worstPerformancePercentage = Double.POSITIVE_INFINITY;

        for (Asset asset : assets) {
            Optional<Performance> performanceOpt = performanceRepository.findLatestPerformanceByAssetId(asset.getId());

            if (performanceOpt.isPresent()) {
                Performance performance = performanceOpt.get();
                double performancePercentage = performance.getPerformancePercentage();

                if (performancePercentage > bestPerformancePercentage) {
                    bestAsset = asset;
                    bestPerformance = performance;
                    bestPerformancePercentage = performancePercentage;
                }

                if (performancePercentage < worstPerformancePercentage) {
                    worstAsset = asset;
                    worstPerformance = performance;
                    worstPerformancePercentage = performancePercentage;
                }
            }
        }

        if (bestAsset == null || worstAsset == null) {
            bestPerformancePercentage = 0.0;
            worstPerformancePercentage = 0.0;
        }

        return new PerformanceResult(
                bestAsset,
                worstAsset,
                bestPerformance,
                worstPerformance,
                bestPerformancePercentage,
                worstPerformancePercentage
        );
    }

}