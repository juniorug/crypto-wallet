package com.postfinance.cryptowallet.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference
    private List<Asset> assets;

    @Transient
    private BigDecimal totalValue;

    @Transient
    private Asset bestAsset;

    @Transient
    private Performance bestPerformance;

    @Transient
    private Asset worstAsset;

    @Transient
    private Performance worstPerformance;

}