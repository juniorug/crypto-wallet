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

    @PostConstruct
    public void initializeCache() {
        symbolToIdMap = coincapService.getAllAssets().stream()
                .collect(Collectors.toMap(Asset::getSymbol, Asset::getId));
    }

    public String getIdBySymbol(String symbol) {
        return symbolToIdMap.get(symbol);
    }
}