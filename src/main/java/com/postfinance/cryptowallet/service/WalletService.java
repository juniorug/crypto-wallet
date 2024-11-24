package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.dto.WalletResponse;
import com.postfinance.cryptowallet.model.Asset;
import com.postfinance.cryptowallet.model.Performance;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.repository.AssetRepository;
import com.postfinance.cryptowallet.repository.PerformanceRepository;
import com.postfinance.cryptowallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AssetRepository assetRepository;
    private final PerformanceRepository performanceRepository;

    /**
     * Retrieves the wallet and calculates the total financial value of the assets
     * in the wallet along with the best and worst performing assets.
     * @param walletId ID of the wallet to be processed
     * @return a WalletResponse containing the total value, best performing asset, and worst performing asset
     */
    public WalletResponse calculateWalletValue(Long walletId) {

        walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<Asset> assets = assetRepository.findByWalletId(walletId);

        double totalValue = 0.0;
        Asset bestAsset = null;
        Asset worstAsset = null;

        for (Asset asset : assets) {
            double assetValue = asset.getQuantity().multiply(asset.getPrice()).doubleValue();
            totalValue += assetValue;

            Optional<Performance> performanceOpt = performanceRepository.findLatestPerformanceByAssetId(asset.getId());
            Performance performance = performanceOpt.orElseThrow(() -> new RuntimeException("Performance data not found"));

            if (bestAsset == null || performance.getPerformancePercentage() > bestAsset.getPerformance().getPerformancePercentage()) {
                bestAsset = asset;
            }
            if (worstAsset == null || performance.getPerformancePercentage() < worstAsset.getPerformance().getPerformancePercentage()) {
                worstAsset = asset;
            }
        }

        // Return the response with total value and best/worst assets
        return new WalletResponse(totalValue, bestAsset, worstAsset);
    }
}
