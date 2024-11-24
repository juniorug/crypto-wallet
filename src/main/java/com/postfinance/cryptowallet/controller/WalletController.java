package com.postfinance.cryptowallet.controller;

import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<String> greetings() {
        return ResponseEntity.ok("Hello, World! This is the Crypto Wallet API. Use /wallets to manage your wallets. ^^");
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        Wallet createdWallet = walletService.createWallet(wallet);
        return ResponseEntity.ok(createdWallet);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWalletDetails(@PathVariable Long walletId) {
        Wallet wallet = walletService.getWalletDetails(walletId);
        return ResponseEntity.ok(wallet);
    }

    @PutMapping("/{walletId}/update-prices")
    public ResponseEntity<Void> updatePrices(@PathVariable Long walletId) {
        walletService.updateWalletData(walletId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }
}
