package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.dto.coincap.AssetHistoryResponse;
import com.postfinance.cryptowallet.dto.coincap.AssetResponse;
import com.postfinance.cryptowallet.mapper.WalletMapper;
import com.postfinance.cryptowallet.model.Asset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CoincapService {

    private final RestTemplate restTemplate;
    private final WalletMapper walletMapper;

    private static final String ASSETS_URL = "/assets/";

    @Value("${coincap.api.url}")
    private String apiUrl;

    @Value("${coincap.api.key}")
    private String apiKey;

    public List<Asset> getAllAssets() {
        String url = apiUrl + ASSETS_URL;
        log.debug("CoincapService.getAllAssets called. url: {}. ", url);
        ResponseEntity<AssetResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, generateHttpRequestWithHeader(), AssetResponse.class);
        AssetResponse response = responseEntity.getBody();
        return walletMapper.coincapAssetsToAssets(response.getData());
    }

    // Fetch the latest price of an asset from Coincap API
    public Double getLatestPrice(String assetSymbol) {
        String url = apiUrl + ASSETS_URL + assetSymbol + "/history?interval=d1";
        log.debug("CoincapService.getLatestPrice called. assetSymbol: {},  url: {}.  ", assetSymbol, url);
        return fetchPriceFromApi(url);
    }

    // Fetch historical price for a given asset on a specific date
    public Double getHistoricalPrice(String assetSymbol, String date) {
        String url = apiUrl + ASSETS_URL + assetSymbol + "/history?interval=d1&start=" + date + "&end=" + date;
        log.debug("CoincapService.getHistoricalPrice called. assetSymbol: {},  date: {}, url: {}.  ", assetSymbol, date, url);
        return fetchPriceFromApi(url);
    }

    /**
     * Common method to fetch the price from Coincap API.
     */
    private Double fetchPriceFromApi(String url) {
        try {
            log.debug("CoincapService.fetchPriceFromApi called. url: {}.  ", url);
            ResponseEntity<AssetHistoryResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, generateHttpRequestWithHeader(), AssetHistoryResponse.class);
            AssetHistoryResponse response = responseEntity.getBody();

            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                String priceUsd = response.getData().get(0).getPriceUsd();
                return Double.parseDouble(priceUsd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpEntity<String> generateHttpRequestWithHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        return new HttpEntity<>(headers);
    }
}
