package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.model.Asset;
import com.postfinance.cryptowallet.model.Performance;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.repository.AssetRepository;
import com.postfinance.cryptowallet.repository.PerformanceRepository;
import com.postfinance.cryptowallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final AssetRepository assetRepository;
    private final PerformanceRepository performanceRepository;
    private final WalletRepository walletRepository;
    private final CoincapService coincapService;
    private static final String WALLET_NOT_FOUND = "Wallet not found";

    public Wallet createWallet(Wallet wallet) {
        Wallet savedWallet = walletRepository.save(wallet);
        for (Asset asset : wallet.getAssets()) {
            if (asset.getPrice() == null) {
                Double latestPrice = coincapService.getLatestPrice(asset.getSymbol());
                asset.setPrice(latestPrice != null ? BigDecimal.valueOf(latestPrice) : BigDecimal.ZERO);
            }
            asset.setWallet(savedWallet);
            assetRepository.save(asset);
        }
        return savedWallet;
    }

    public Wallet getWalletDetails(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException(WALLET_NOT_FOUND));
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

        log.info("Starting to update data for wallet ID: {}", walletId);

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException(WALLET_NOT_FOUND));

        log.info("Wallet {} found. Updating asset prices...", walletId);

        wallet.getAssets().forEach(asset -> {
            Double latestPriceDouble = coincapService.getLatestPrice(asset.getSymbol());

            if (latestPriceDouble != null) {
                BigDecimal latestPrice = BigDecimal.valueOf(latestPriceDouble);
                log.info("Updating price for asset {}: new price is {}", asset.getSymbol(), latestPrice);
                asset.setPrice(latestPrice);
                assetRepository.save(asset);
            } else {
                log.warn("Could not fetch latest price for asset {}", asset.getSymbol());
            }
        });

        calculateAndSaveWalletMetrics(walletId);
        log.info("Successfully updated wallet data for wallet ID: {}", walletId);
    }

    /**
     * Fetch detailed wallet information, including total value,
     * best and worst-performing assets.
     * Saves the metrics to the database.
     * @param walletId ID of the wallet
     *
     */
    private void calculateAndSaveWalletMetrics(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException(WALLET_NOT_FOUND));

        List<Asset> assets = assetRepository.findByWalletId(walletId);

        BigDecimal totalValue = BigDecimal.ZERO;
        Asset bestAsset = null;
        Asset worstAsset = null;
        Performance bestPerformance = null;
        Performance worstPerformance = null;

        double bestPerformancePercentage = Double.MIN_VALUE;
        double worstPerformancePercentage = Double.MAX_VALUE;

        for (Asset asset : assets) {
            BigDecimal assetValue = asset.getQuantity().multiply(asset.getPrice());
            totalValue = totalValue.add(assetValue);

            Optional<Performance> performanceOpt = performanceRepository.findLatestPerformanceByAssetId(asset.getId());

            if (performanceOpt.isPresent()) {
                Performance performance = performanceOpt.get();
                double performancePercentage = performance.getPerformancePercentage();

                // Determine the best performing asset
                if (bestAsset == null || performancePercentage > bestPerformancePercentage) {
                    bestAsset = asset;
                    bestPerformance = performance;
                    bestPerformancePercentage = performancePercentage;
                }

                // Determine the worst performing asset
                if (worstAsset == null || performancePercentage < worstPerformancePercentage) {
                    worstAsset = asset;
                    worstPerformance = performance;
                    worstPerformancePercentage = performancePercentage;
                }
            }
        }

        wallet.setTotalValue(totalValue);
        wallet.setBestAsset(bestAsset);
        wallet.setBestPerformance(bestPerformance);
        wallet.setWorstAsset(worstAsset);
        wallet.setWorstPerformance(worstPerformance);

        walletRepository.save(wallet);
    }
}