package com.postfinance.cryptowallet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> assets;

    @Transient // Not persisted
    private BigDecimal totalValue;

    @Transient // Not persisted
    private Asset bestAsset;

    @Transient // Not persisted
    private Performance bestPerformance;

    @Transient // Not persisted
    private Asset worstAsset;

    @Transient // Not persisted
    private Performance worstPerformance;

}