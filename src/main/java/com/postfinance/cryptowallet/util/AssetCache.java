package com.postfinance.cryptowallet.util;

import com.postfinance.cryptowallet.model.Asset;
import com.postfinance.cryptowallet.service.CoincapService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AssetCache {
    private final CoincapService coincapService;
    private Map<String, String> symbolToIdMap;

    private Map<String, String> symbolToNameMap;

    @PostConstruct
    public void initializeCache() {
        symbolToIdMap = coincapService.getAllAssets().stream()
                .collect(Collectors.toMap(Asset::getSymbol, Asset::getExternalId));

        symbolToNameMap = coincapService.getAllAssets().stream()
                .collect(Collectors.toMap(Asset::getSymbol, Asset::getName));
    }

    public String getIdBySymbol(String symbol) {
        return symbolToIdMap.get(symbol);
    }

    public String getNameBySymbol(String symbol) {
        return symbolToNameMap.get(symbol);
    }
}