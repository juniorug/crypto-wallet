package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.dto.coincap.AssetHistory;
import com.postfinance.cryptowallet.dto.coincap.AssetHistoryResponse;
import com.postfinance.cryptowallet.dto.coincap.AssetResponse;
import com.postfinance.cryptowallet.dto.coincap.CoincapAsset;
import com.postfinance.cryptowallet.exception.CoincapApiException;
import com.postfinance.cryptowallet.mapper.WalletMapper;
import com.postfinance.cryptowallet.model.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CoincapServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private CoincapService coincapService;

    private static final String API_URL = "https://api.coincap.io/v2";
    private static final String API_KEY = "some-api-key";

    @BeforeEach
    void setUp() {
        // Setup dos mocks, se necessário
    }

    @Test
    void testGetAllAssets() {
        // Mock do RestTemplate
        AssetResponse assetResponse = new AssetResponse();
        CoincapAsset coincapAsset = new CoincapAsset();
        coincapAsset.setSymbol("BTC");
        coincapAsset.setName("Bitcoin");
        assetResponse.setData(Collections.singletonList(coincapAsset));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetResponse.class)))
                .thenReturn(new ResponseEntity<>(assetResponse, HttpStatus.OK));

        Asset asset = new Asset();
        asset.setSymbol("BTC");
        asset.setName("Bitcoin");
        List<Asset> assets = Collections.singletonList(asset);
        when(walletMapper.coincapAssetsToAssets(any())).thenReturn(assets);

        List<Asset> result = coincapService.getAllAssets();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BTC", result.get(0).getSymbol());

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetResponse.class));
        verify(walletMapper).coincapAssetsToAssets(any());
    }

    @Test
    void testGetLatestPrice() {
        AssetHistoryResponse assetHistoryResponse = new AssetHistoryResponse();
        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setPriceUsd("10000");
        assetHistory.setTime(1L);
        assetHistoryResponse.setData(Collections.singletonList(assetHistory));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class)))
                .thenReturn(new ResponseEntity<>(assetHistoryResponse, HttpStatus.OK));

        Double price = coincapService.getLatestPrice("BTC");

        assertNotNull(price);
        assertEquals(10000.0, price);

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class));
    }

    @Test
    void testGetHistoricalPrice() {
        AssetHistoryResponse assetHistoryResponse = new AssetHistoryResponse();
        AssetHistory assetHistory = new AssetHistory();
        assetHistory.setPriceUsd("10000");
        assetHistory.setTime(1L);
        assetHistoryResponse.setData(Collections.singletonList(assetHistory));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class)))
                .thenReturn(new ResponseEntity<>(assetHistoryResponse, HttpStatus.OK));

        Double price = coincapService.getHistoricalPrice("BTC", "2024-01-01");

        assertNotNull(price);
        assertEquals(5000.0, price);

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class));
    }

    @Test
    void testGetLatestPrice_ShouldThrowException_WhenApiFails() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class)))
                .thenThrow(new RuntimeException("API failed"));

        assertThrows(CoincapApiException.class, () -> {
            coincapService.getLatestPrice("BTC");
        });
    }
}
