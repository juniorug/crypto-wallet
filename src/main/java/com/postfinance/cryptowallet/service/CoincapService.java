package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.dto.AssetHistoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class CoincapService {

    private final RestTemplate restTemplate;

    @Value("${coincap.api.url}")
    private String apiUrl;

    @Value("${coincap.api.key}")
    private String apiKey;

    // Fetch the latest price of an asset from Coincap API
    public Double getLatestPrice(String assetSymbol) {
        String url = apiUrl + "/assets/" + assetSymbol + "/history?interval=d1";
        return fetchPriceFromApi(url);
    }

    // Fetch historical price for a given asset on a specific date
    public Double getHistoricalPrice(String assetSymbol, String date) {
        String url = apiUrl + "/assets/" + assetSymbol + "/history?interval=d1&start=" + date + "&end=" + date;
        return fetchPriceFromApi(url);
    }

    /**
     * Common method to fetch the price from Coincap API.
     */
    private Double fetchPriceFromApi(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<AssetHistoryResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, request, AssetHistoryResponse.class);
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
}
