package com.postfinance.cryptowallet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_history")
@Data
@NoArgsConstructor
public class WalletHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id", nullable = false)
    private Long walletId;

    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

}