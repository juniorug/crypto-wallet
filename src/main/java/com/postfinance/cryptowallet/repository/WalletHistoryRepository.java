package com.postfinance.cryptowallet.repository;

import com.postfinance.cryptowallet.model.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {
    List<WalletHistory> findByWalletIdOrderByTimestampDesc(Long walletId);
}
