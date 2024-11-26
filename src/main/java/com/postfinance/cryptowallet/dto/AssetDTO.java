package com.postfinance.cryptowallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetDTO implements Serializable {
    private String id;
    private String name;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;
}
