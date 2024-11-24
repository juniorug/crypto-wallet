package com.postfinance.cryptowallet.repository;

import com.postfinance.cryptowallet.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    // Custom method to retrieve assets by walletId
    List<Asset> findByWalletId(Long walletId);
}