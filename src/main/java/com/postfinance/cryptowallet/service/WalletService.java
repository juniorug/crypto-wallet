package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.model.Asset;
import com.postfinance.cryptowallet.model.Performance;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.repository.AssetRepository;
import com.postfinance.cryptowallet.repository.PerformanceRepository;
import com.postfinance.cryptowallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;
    private final PerformanceRepository performanceRepository;

    public Wallet calculateWalletValue(Long walletId) {

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