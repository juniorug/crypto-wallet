package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.model.WalletHistory;
import com.postfinance.cryptowallet.repository.WalletHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletHistoryService {

    private final WalletHistoryRepository walletHistoryRepository;

    public void saveWalletHistory(Long walletId, BigDecimal totalValue) {
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setWalletId(walletId);
        walletHistory.setTotalValue(totalValue);
        walletHistory.setTimestamp(LocalDateTime.now());

        walletHistoryRepository.save(walletHistory);
    }

    public List<WalletHistory> getWalletHistory(Long walletId) {
        return walletHistoryRepository.findByWalletIdOrderByTimestampDesc(walletId);
    }
}
