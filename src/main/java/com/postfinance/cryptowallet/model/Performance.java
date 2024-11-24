package com.postfinance.cryptowallet.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance")
@Data
@NoArgsConstructor
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @Column(name = "percentage_change", nullable = false, precision = 10, scale = 2)
    private BigDecimal percentageChange;

    @Column(name = "current_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "previous_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal previousValue;

    private LocalDateTime timestamp;

    public double getPerformancePercentage() {
        if (previousValue == null || currentValue == null || previousValue.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        // Calculate the percentage change
        BigDecimal change = currentValue.subtract(previousValue);
        return change.divide(previousValue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
    }

}