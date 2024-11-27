package com.postfinance.cryptowallet.repository;

import com.postfinance.cryptowallet.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query(value = "SELECT * FROM performance p WHERE p.asset_id = :assetId ORDER BY p.timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<Performance> findLatestPerformanceByAssetId(Long assetId);
}