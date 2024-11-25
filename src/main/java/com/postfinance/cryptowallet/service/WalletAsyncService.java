package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.model.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletAsyncService {

    private final WalletService walletService;

    @Async
    public void updateWalletData(Long walletId) {
        walletService.updateWalletData(walletId);
    }

    @Async
    public void updateAllWalletsData() {
        log.info("WalletAsyncService.updateAllWalletsData");
        List<Wallet> wallets = walletService.findAllWallets();
        wallets.forEach(wallet -> walletService.updateWalletData(wallet.getId()));
    }
}
