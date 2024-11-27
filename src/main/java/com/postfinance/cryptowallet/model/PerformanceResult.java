package com.postfinance.cryptowallet.model;

import lombok.Getter;

@Getter
public record PerformanceResult(Asset bestAsset, Asset worstAsset, Performance bestPerformance,
                                Performance worstPerformance, double bestPerformancePercentage,
                                double worstPerformancePercentage) {
}
