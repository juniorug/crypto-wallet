package com.postfinance.cryptowallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDTO implements Serializable {
    private Long id;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
}
