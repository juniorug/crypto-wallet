package com.postfinance.cryptowallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletPerformanceDTO implements Serializable {

    //private Long walletId;
    private BigDecimal total;
    private String bestAsset;
    private BigDecimal bestPerformance;
    private String worstAsset;
    private BigDecimal worstPerformance;

}
