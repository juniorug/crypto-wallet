package com.postfinance.cryptowallet.controller;

import com.postfinance.cryptowallet.UnitTest;
import com.postfinance.cryptowallet.dto.WalletDTO;
import com.postfinance.cryptowallet.dto.WalletPerformanceDTO;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.model.WalletHistory;
import com.postfinance.cryptowallet.service.WalletAsyncService;
import com.postfinance.cryptowallet.service.WalletHistoryService;
import com.postfinance.cryptowallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@UnitTest
class WalletControllerTest {

    private static final double DOUBLE_100 = 100.0;
    private static final long LONG_1L = 1L;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletAsyncService walletAsyncService;

    @Mock
    private WalletHistoryService walletHistoryService;

    @InjectMocks
    private WalletController walletController;

    @Test
    void testGreetings() {
        ResponseEntity<String> response = walletController.greetings();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello, World! This is the Crypto Wallet API. Use /wallets to manage your wallets. ^^", response.getBody());
    }

    @Test
    void testCreateWallet() {
        Wallet wallet = new Wallet();
        wallet.setId(LONG_1L);
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setId(LONG_1L);

        WalletPerformanceDTO mockPerformanceDTO = new WalletPerformanceDTO();
        mockPerformanceDTO.setTotal(BigDecimal.valueOf(DOUBLE_100));
        CompletableFuture<WalletPerformanceDTO> futureResponse = CompletableFuture.completedFuture(mockPerformanceDTO);

        when(walletService.createWallet(wallet)).thenReturn(walletDTO);
        when(walletAsyncService.updateWalletData(wallet.getId())).thenReturn(futureResponse);

        WalletPerformanceDTO result = futureResponse.join();

        ResponseEntity<WalletPerformanceDTO> response = walletController.createWallet(wallet);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(DOUBLE_100), result.getTotal());

    }

    @Test
    void testGetWalletDetails() {
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setId(LONG_1L);

        when(walletService.getWalletDetails(LONG_1L)).thenReturn(walletDTO);

        ResponseEntity<WalletDTO> response = walletController.getWalletDetails(LONG_1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void testDeleteWallet() {
        doNothing().when(walletService).deleteWallet(LONG_1L);

        ResponseEntity<Void> response = walletController.deleteWallet(LONG_1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(walletService, times(1)).deleteWallet(LONG_1L);
    }

    @Test
    void testUpdateWalletData() {
        WalletPerformanceDTO mockPerformanceDTO = new WalletPerformanceDTO();
        mockPerformanceDTO.setTotal(BigDecimal.valueOf(DOUBLE_100));
        CompletableFuture<WalletPerformanceDTO> futureResponse = CompletableFuture.completedFuture(mockPerformanceDTO);

        when(walletAsyncService.updateWalletData(LONG_1L)).thenReturn(futureResponse);

        WalletPerformanceDTO result = futureResponse.join();

        ResponseEntity<WalletPerformanceDTO> response = walletController.updateWalletData(LONG_1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(BigDecimal.valueOf(DOUBLE_100), result.getTotal());
    }

    @Test
    void testUpdateAllWalletsData() {
        doNothing().when(walletAsyncService).updateAllWalletsData();

        ResponseEntity<String> response = walletController.updateAllWalletsData();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Wallet data update process started successfully!", response.getBody());
    }

    @Test
    void testGetWalletPerformance() {
        WalletPerformanceDTO performanceDTO = new WalletPerformanceDTO();

        when(walletService.calculateAndSaveWalletMetrics(LONG_1L)).thenReturn(performanceDTO);

        ResponseEntity<WalletPerformanceDTO> response = walletController.getWalletPerformance(LONG_1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetWalletHistory() {
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setId(LONG_1L);
        List<WalletHistory> history = List.of(walletHistory);

        when(walletHistoryService.getWalletHistory(LONG_1L)).thenReturn(history);

        ResponseEntity<List<WalletHistory>> response = walletController.getWalletHistory(LONG_1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetWalletHistoryEmpty() {
        when(walletHistoryService.getWalletHistory(LONG_1L)).thenReturn(List.of());

        ResponseEntity<List<WalletHistory>> response = walletController.getWalletHistory(LONG_1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
