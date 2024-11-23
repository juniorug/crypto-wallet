package com.postfinance.crypto_wallet.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "performance")
@Data
@NoArgsConstructor
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "percentage_change", nullable = false, precision = 10, scale = 2)
    private BigDecimal percentageChange;

    @Column(name = "current_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "previous_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal previousValue;

}