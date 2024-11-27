package com.postfinance.cryptowallet.controller;

import com.postfinance.cryptowallet.UnitTest;
import com.postfinance.cryptowallet.dto.WalletDTO;
import com.postfinance.cryptowallet.dto.WalletPerformanceDTO;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.model.WalletHistory;
import com.postfinance.cryptowallet.service.WalletAsyncService;
import com.postfinance.cryptowallet.service.WalletHistoryService;
import com.postfinance.cryptowallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(WalletController.class)
@UnitTest
@Disabled
class WalletControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletAsyncService walletAsyncService;

    @Mock
    private WalletHistoryService walletHistoryService;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
    }

    @Test
    void testGreetings() throws Exception {
        mockMvc.perform(get("/wallets"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World! This is the Crypto Wallet API. Use /wallets to manage your wallets. ^^"));
    }

    /*@Test
    void testCreateWallet() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setId(1L);

        when(walletService.createWallet(wallet)).thenReturn(walletDTO);

        mockMvc.perform(post("/wallets")
                        .contentType("application/json")
                        .content("{\"id\": 1}"))  // Exemplo de conteúdo JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetWalletDetails() throws Exception {
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setId(1L);

        when(walletService.getWalletDetails(1L)).thenReturn(walletDTO);

        mockMvc.perform(get("/wallets/{walletId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteWallet() throws Exception {
        doNothing().when(walletService).deleteWallet(1L);

        mockMvc.perform(delete("/wallets/{walletId}", 1L))
                .andExpect(status().isNoContent());

        verify(walletService, times(1)).deleteWallet(1L);
    }

    @Test
    void testUpdateWalletData() throws Exception {
        doNothing().when(walletAsyncService).updateWalletData(1L);

        mockMvc.perform(put("/wallets/{walletId}", 1L))
                .andExpect(status().isNoContent());

        verify(walletAsyncService, times(1)).updateWalletData(1L);
    }

    @Test
    void testUpdateAllWalletsData() throws Exception {
        doNothing().when(walletAsyncService).updateAllWalletsData();

        mockMvc.perform(put("/wallets/update-data"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet data update process started successfully!"));

        verify(walletAsyncService, times(1)).updateAllWalletsData();
    }

    @Test
    void testGetWalletPerformance() throws Exception {
        WalletPerformanceDTO performanceDTO = new WalletPerformanceDTO();
        //performanceDTO.setWalletId(1L);

        when(walletService.calculateAndSaveWalletMetrics(1L)).thenReturn(performanceDTO);

        mockMvc.perform(get("/wallets/{walletId}/performance", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(1));
    }

    @Test
    void testGetWalletHistory() throws Exception {
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setId(1L);
        List<WalletHistory> history = List.of(walletHistory);

        when(walletHistoryService.getWalletHistory(1L)).thenReturn(history);

        mockMvc.perform(get("/wallets/{walletId}/history", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }*/

    @Test
    void testGetWalletHistoryEmpty() throws Exception {
        when(walletHistoryService.getWalletHistory(1L)).thenReturn(List.of());

        mockMvc.perform(get("/wallets/{walletId}/history", 1L))
                .andExpect(status().isNoContent());
    }
}
