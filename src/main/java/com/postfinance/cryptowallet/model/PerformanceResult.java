package com.postfinance.cryptowallet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PerformanceResult {
    private final Asset bestAsset;
    private final Asset worstAsset;
    private final Performance bestPerformance;
    private final Performance worstPerformance;
    private final double bestPerformancePercentage;
    private final double worstPerformancePercentage;
}
