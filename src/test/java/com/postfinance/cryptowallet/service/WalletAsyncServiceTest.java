package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.UnitTest;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.dto.WalletPerformanceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.EnableAsync;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@EnableAsync
@UnitTest
class WalletAsyncServiceTest {

    @InjectMocks
    private WalletAsyncService walletAsyncService;

    @Mock
    private WalletService walletService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    //TODO: fix tests for this class.
    /*@Test
     void testUpdateWalletData() throws Exception {
        Long walletId = 1L;

        WalletPerformanceDTO mockPerformanceDTO = new WalletPerformanceDTO();
        mockPerformanceDTO.setTotal(BigDecimal.valueOf(100.0));  // Usando BigDecimal aqui

        CompletableFuture<WalletPerformanceDTO> futureResponse = CompletableFuture.completedFuture(mockPerformanceDTO);
        when(walletService.updateWalletData(walletId)).thenReturn(futureResponse);

        CompletableFuture<WalletPerformanceDTO> future = walletAsyncService.updateWalletData(walletId);

        WalletPerformanceDTO result = future.get(); // Espera a conclusão do CompletableFuture

        assert result != null;
        assert result.getTotal().compareTo(BigDecimal.valueOf(100.0)) == 0; // Comparação correta com BigDecimal

        verify(walletService, times(1)).updateWalletData(walletId);
    }

    @Test
    void testUpdateAllWalletsData() {
        List<Wallet> mockWallets = List.of(new Wallet(), new Wallet());
        when(walletService.findAllWallets()).thenReturn(mockWallets);

        walletAsyncService.updateAllWalletsData();

        for (Wallet wallet : mockWallets) {
            verify(walletService, times(1)).updateWalletData(wallet.getId());
        }
    }

    @Test
    void testUpdateWalletPricesPeriodically() {
        Wallet wallet1 = new Wallet();
        wallet1.setId(1L);
        Wallet wallet2 = new Wallet();
        wallet2.setId(2L);        WalletPerformanceDTO mockPerformanceDTO = new WalletPerformanceDTO();
        mockPerformanceDTO.setTotal(BigDecimal.valueOf(100.0));

        List<Wallet> mockWallets = List.of(wallet1, wallet2);

        when(walletService.findAllWallets()).thenReturn(mockWallets);

        CompletableFuture<WalletPerformanceDTO> futureResponse = CompletableFuture.completedFuture(mockPerformanceDTO);
        when(walletService.updateWalletData(any())).thenReturn(futureResponse);

        walletAsyncService.updateWalletPricesPeriodically();

        for (Wallet wallet : mockWallets) {
            verify(walletService, times(1)).updateWalletData(wallet.getId());
        }
    }*/
}
