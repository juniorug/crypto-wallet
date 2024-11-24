package com.postfinance.cryptowallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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
        String url = apiUrl + "/assets/" + assetSymbol + "/history?interval=d1";  // Modify endpoint if necessary
        try {
            // Call the API and fetch the response
            Map<String, Object> response = restTemplate.getForObject(url, HashMap.class);
            // Extract and return the latest price
            if (response != null && response.get("data") != null) {
                Map<String, Object> latestData = (Map<String, Object>) response.get("data");
                return (Double) latestData.get("priceUsd");
            }
        } catch (Exception e) {
            e.printStackTrace();  // Handle errors properly in production
        }
        return null;
    }

    // Fetch historical price for a given asset on a specific date
    public Double getHistoricalPrice(String assetSymbol, String date) {
        String url = apiUrl + "/assets/" + assetSymbol + "/history?interval=d1&start=" + date + "&end=" + date; // Modify date range logic as needed
        try {
            // Call the API and fetch the response
            Map<String, Object> response = restTemplate.getForObject(url, HashMap.class);
            // Extract and return the historical price
            if (response != null && response.get("data") != null) {
                Map<String, Object> historicalData = (Map<String, Object>) response.get("data");
                return (Double) historicalData.get("priceUsd");
            }
        } catch (Exception e) {
            e.printStackTrace();  // Handle errors properly in production
        }
        return null;
    }
}
