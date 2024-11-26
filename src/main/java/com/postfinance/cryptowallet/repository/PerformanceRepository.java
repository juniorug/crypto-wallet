package com.postfinance.cryptowallet.repository;

import com.postfinance.cryptowallet.model.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query("SELECT p FROM Performance p WHERE p.asset.id = :assetId ORDER BY p.timestamp DESC")
    Optional<Performance> findLatestPerformanceByAssetId(String assetId);
}