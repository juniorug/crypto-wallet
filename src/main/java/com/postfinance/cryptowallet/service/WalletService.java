package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.model.Asset;
import com.postfinance.cryptowallet.model.Performance;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.repository.AssetRepository;
import com.postfinance.cryptowallet.repository.PerformanceRepository;
import com.postfinance.cryptowallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final AssetRepository assetRepository;
    private final PerformanceRepository performanceRepository;
    private final WalletRepository walletRepository;
    private final CoincapService coincapService;

    public Wallet createWallet(Wallet wallet) {
        Wallet savedWallet = walletRepository.save(wallet);
        for (Asset asset : wallet.getAssets()) {
            asset.setWallet(savedWallet);
            assetRepository.save(asset);
        }
        return savedWallet;
    }


    @Async
    @Transactional
    public void updateWalletData(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        wallet.getAssets().forEach(asset -> {
            Double latestPriceDouble = coincapService.getLatestPrice(asset.getSymbol());

            if (latestPriceDouble != null) {
                BigDecimal latestPrice = BigDecimal.valueOf(latestPriceDouble);
                asset.setPrice(latestPrice);
                assetRepository.save(asset);
            }
        });

        BigDecimal totalValue = wallet.getAssets().stream()
                .map(asset -> asset.getPrice().multiply(asset.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        wallet.setTotalValue(totalValue);
        walletRepository.save(wallet);
    }

    @Async
    public void updateAllWalletsData() {
        List<Wallet> wallets = walletRepository.findAll();
        wallets.forEach(wallet -> updateWalletData(wallet.getId()));
    }

    public void deleteWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        for (Asset asset : wallet.getAssets()) {
            assetRepository.delete(asset);
        }

        walletRepository.delete(wallet);
    }

    /**
     * Fetch detailed wallet information, including total value,
     * best and worst-performing assets.
     * @param walletId ID of the wallet
     *
     * @return Wallet with detailed information
     */
    public Wallet getWalletDetails(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

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

        return wallet;
    }
}