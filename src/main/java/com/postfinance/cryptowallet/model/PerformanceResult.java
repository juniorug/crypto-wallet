package com.postfinance.cryptowallet.model;

public record PerformanceResult(Asset bestAsset, Asset worstAsset, Performance bestPerformance,
                                Performance worstPerformance, double bestPerformancePercentage,
                                double worstPerformancePercentage) {
}
