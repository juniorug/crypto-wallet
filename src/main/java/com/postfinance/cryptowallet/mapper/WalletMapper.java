package com.postfinance.cryptowallet.mapper;

import com.postfinance.cryptowallet.dto.coincap.CoincapAsset;
import com.postfinance.cryptowallet.model.Asset;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.dto.AssetDTO;
import com.postfinance.cryptowallet.dto.WalletDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class WalletMapper {

    public AssetDTO toAssetDTO(Asset asset) {
        return AssetDTO.builder()
                .id(asset.getId())
                .externalId(asset.getExternalId())
                .name(asset.getName())
                .symbol(asset.getSymbol())
                .quantity(asset.getQuantity())
                .price(asset.getPrice())
                .build();
    }

    public List<AssetDTO> toAssetDTOList(List<Asset> assets) {
        return assets.stream()
                .map(this::toAssetDTO)
                .toList();
    }

    // Converter Wallet para WalletDTO
    public WalletDTO toWalletDTO(Wallet wallet) {
        return WalletDTO.builder()
                .id(wallet.getId())
                .name(wallet.getName())
                .assets(toAssetDTOList(wallet.getAssets()))
                .build();
    }

    public Asset coincapAssetToAsset(CoincapAsset coincapAsset) {
        return Asset.builder()
                .externalId(coincapAsset.getId())
                .name(coincapAsset.getName())
                .symbol(coincapAsset.getSymbol())
                .build();
    }

    public List<Asset> coincapAssetsToAssets(List<CoincapAsset> coincapAssets) {
        return Optional.ofNullable(coincapAssets)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::coincapAssetToAsset)
                .toList();
    }

}
