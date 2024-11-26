package com.postfinance.cryptowallet.controller;

import com.postfinance.cryptowallet.dto.WalletDTO;
import com.postfinance.cryptowallet.dto.WalletPerformanceDTO;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.service.WalletAsyncService;
import com.postfinance.cryptowallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final WalletAsyncService walletAsyncService;

    @GetMapping
    public ResponseEntity<String> greetings() {
        return ResponseEntity.ok("Hello, World! This is the Crypto Wallet API. Use /wallets to manage your wallets. ^^");
    }

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet(@RequestBody Wallet wallet) {
        WalletDTO createdWallet = walletService.createWallet(wallet);
        return ResponseEntity.ok(createdWallet);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletDTO> getWalletDetails(@PathVariable Long walletId) {
        WalletDTO wallet = walletService.getWalletDetails(walletId);
        return ResponseEntity.ok(wallet);
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{walletId}")
    public ResponseEntity<Void> updateWalletData(@PathVariable Long walletId) {
        walletAsyncService.updateWalletData(walletId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-data")
    public ResponseEntity<String> updateAllWalletsData() {
        walletAsyncService.updateAllWalletsData();
        return ResponseEntity.ok("Wallet data update process started successfully!");
    }

    @GetMapping("/{walletId}/performance")
    public ResponseEntity<WalletPerformanceDTO> getWalletPerformance(@PathVariable Long walletId) {
        WalletPerformanceDTO performanceDTO = walletService.calculateAndSaveWalletMetrics(walletId);
        return ResponseEntity.ok(performanceDTO);
    }
}
