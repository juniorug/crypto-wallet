package com.postfinance.cryptowallet.controller;

import com.postfinance.cryptowallet.dto.WalletDTO;
import com.postfinance.cryptowallet.dto.WalletPerformanceDTO;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.model.WalletHistory;
import com.postfinance.cryptowallet.service.WalletAsyncService;
import com.postfinance.cryptowallet.service.WalletHistoryService;
import com.postfinance.cryptowallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet Management", description = "Operations related to managing wallets and their performance.")
public class WalletController {

    private final WalletService walletService;
    private final WalletAsyncService walletAsyncService;
    private final WalletHistoryService walletHistoryService;

    @GetMapping
    @Operation(summary = "Greetings", description = "Returns a hello message with information about the Crypto Wallet API.")
    public ResponseEntity<String> greetings() {
        return ResponseEntity.ok("Hello, World! This is the Crypto Wallet API. Use /wallets to manage your wallets. ^^");
    }

    @PostMapping
    @Operation(summary = "Create a new wallet", description = "Creates a new wallet based on the provided wallet data.")
    @ApiResponse(responseCode = "200", description = "Wallet created successfully", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    public ResponseEntity<WalletDTO> createWallet(@RequestBody Wallet wallet) {
        WalletDTO createdWallet = walletService.createWallet(wallet);
        return ResponseEntity.ok(createdWallet);
    }

    @GetMapping("/{walletId}")
    @Operation(summary = "Get wallet details", description = "Fetches the details of a specific wallet by its ID.")
    @ApiResponse(responseCode = "200", description = "Wallet details retrieved successfully", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    public ResponseEntity<WalletDTO> getWalletDetails(@PathVariable Long walletId) {
        WalletDTO wallet = walletService.getWalletDetails(walletId);
        return ResponseEntity.ok(wallet);
    }

    @DeleteMapping("/{walletId}")
    @Operation(summary = "Delete wallet", description = "Deletes a specific wallet by its ID.")
    @ApiResponse(responseCode = "204", description = "Wallet deleted successfully")
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{walletId}")
    @Operation(summary = "Update wallet data", description = "Triggers an asynchronous update for a wallet's data.")
    @ApiResponse(responseCode = "204", description = "Wallet data updated successfully")
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    public ResponseEntity<Void> updateWalletData(@PathVariable Long walletId) {
        walletAsyncService.updateWalletData(walletId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-data")
    @Operation(summary = "Update all wallets data", description = "Triggers an asynchronous update for the data of all wallets.")
    @ApiResponse(responseCode = "200", description = "Wallet data update process started successfully")
    public ResponseEntity<String> updateAllWalletsData() {
        walletAsyncService.updateAllWalletsData();
        return ResponseEntity.ok("Wallet data update process started successfully!");
    }

    @GetMapping("/{walletId}/performance")
    @Operation(summary = "Get wallet performance", description = "Fetches the performance metrics of a specific wallet.")
    @ApiResponse(responseCode = "200", description = "Wallet performance retrieved successfully", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    public ResponseEntity<WalletPerformanceDTO> getWalletPerformance(@PathVariable Long walletId) {
        WalletPerformanceDTO performanceDTO = walletService.calculateAndSaveWalletMetrics(walletId);
        return ResponseEntity.ok(performanceDTO);
    }

    @GetMapping("/{walletId}/history")
    @Operation(summary = "Get wallet history", description = "Fetches the historical data of a specific wallet.")
    @ApiResponse(responseCode = "200", description = "Wallet history retrieved successfully", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "204", description = "No history found")
    @ApiResponse(responseCode = "404", description = "Wallet not found")
    public ResponseEntity<List<WalletHistory>> getWalletHistory(@PathVariable Long walletId) {
        List<WalletHistory> history = walletHistoryService.getWalletHistory(walletId);
        if (history.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(history);
    }
}
