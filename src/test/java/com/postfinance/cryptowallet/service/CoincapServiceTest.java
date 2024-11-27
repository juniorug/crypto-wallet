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

    // Reusable mock objects
    private CoincapAsset mockCoincapAsset;
    private Asset mockAsset;
    private AssetHistoryResponse mockAssetHistoryResponse;
    private AssetHistory mockAssetHistory;

    @BeforeEach
    void setUp() {
        // Setup common mocks
        mockCoincapAsset = new CoincapAsset();
        mockCoincapAsset.setSymbol("BTC");
        mockCoincapAsset.setName("Bitcoin");
        mockAsset = new Asset();
        mockAsset.setSymbol("BTC");
        mockAsset.setName("Bitcoin");

        mockAssetHistory = new AssetHistory();
        mockAssetHistory.setPriceUsd("10000");
        mockAssetHistory.setTime(1L);
        mockAssetHistoryResponse = new AssetHistoryResponse(Collections.singletonList(mockAssetHistory), 0L);
    }

    // Helper method to mock the restTemplate response
    private <T> void mockRestTemplateResponse(Class<T> responseClass, T response) {
        ResponseEntity<T> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(responseClass)))
                .thenReturn(responseEntity);
    }

    @Test
    void testGetAllAssets() {
        AssetResponse assetResponse = new AssetResponse();
        assetResponse.setData(Collections.singletonList(mockCoincapAsset));

        mockRestTemplateResponse(AssetResponse.class, assetResponse);

        when(walletMapper.coincapAssetsToAssets(any())).thenReturn(Collections.singletonList(mockAsset));

        List<Asset> result = coincapService.getAllAssets();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BTC", result.get(0).getSymbol());

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetResponse.class));
        verify(walletMapper).coincapAssetsToAssets(any());
    }

    @Test
    void testGetLatestPrice() {
        mockRestTemplateResponse(AssetHistoryResponse.class, mockAssetHistoryResponse);

        Double price = coincapService.getLatestPrice("BTC");

        assertNotNull(price);
        assertEquals(10000.0, price);

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class));
    }

    @Test
    void testGetHistoricalPrice() {
        mockRestTemplateResponse(AssetHistoryResponse.class, mockAssetHistoryResponse);

        Double price = coincapService.getHistoricalPrice("BTC", "2024-01-01");

        assertNotNull(price);
        assertEquals(10000.0, price);

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
