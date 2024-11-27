package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.dto.coincap.AssetHistoryResponse;
import com.postfinance.cryptowallet.dto.coincap.AssetResponse;
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
public class CoincapServiceTest {

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
        Asset asset = new Asset();
        asset.setSymbol("BTC");
        asset.setName("Bitcoin");
        assetResponse.setData(Collections.singletonList(asset));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetResponse.class)))
                .thenReturn(new ResponseEntity<>(assetResponse, HttpStatus.OK));

        // Mock do WalletMapper
        List<Asset> assets = Collections.singletonList(asset);
        when(walletMapper.coincapAssetsToAssets(any())).thenReturn(assets);

        // Chamada ao método
        List<Asset> result = coincapService.getAllAssets();

        // Verificações
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BTC", result.get(0).getSymbol());

        // Verificar interações
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetResponse.class));
        verify(walletMapper).coincapAssetsToAssets(any());
    }

    @Test
    void testGetLatestPrice() {
        // Mock da resposta do RestTemplate
        AssetHistoryResponse assetHistoryResponse = new AssetHistoryResponse();
        assetHistoryResponse.setData(Collections.singletonList(new AssetHistoryResponse.Data("1", "10000")));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class)))
                .thenReturn(new ResponseEntity<>(assetHistoryResponse, HttpStatus.OK));

        // Chamada ao método
        Double price = coincapService.getLatestPrice("BTC");

        // Verificações
        assertNotNull(price);
        assertEquals(10000.0, price);

        // Verificar interações
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class));
    }

    @Test
    void testGetHistoricalPrice() {
        // Mock da resposta do RestTemplate
        AssetHistoryResponse assetHistoryResponse = new AssetHistoryResponse();
        assetHistoryResponse.setData(Collections.singletonList(new AssetHistoryResponse.Data("1", "5000")));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class)))
                .thenReturn(new ResponseEntity<>(assetHistoryResponse, HttpStatus.OK));

        // Chamada ao método
        Double price = coincapService.getHistoricalPrice("BTC", "2024-01-01");

        // Verificações
        assertNotNull(price);
        assertEquals(5000.0, price);

        // Verificar interações
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class));
    }

    @Test
    void testGetLatestPrice_ShouldThrowException_WhenApiFails() {
        // Simulando uma falha na API
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssetHistoryResponse.class)))
                .thenThrow(new RuntimeException("API failed"));

        // Esperar que a exceção seja lançada
        assertThrows(CoincapApiException.class, () -> {
            coincapService.getLatestPrice("BTC");
        });
    }
}
