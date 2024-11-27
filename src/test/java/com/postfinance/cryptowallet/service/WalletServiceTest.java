package com.postfinance.cryptowallet.service;

import com.postfinance.cryptowallet.dto.WalletDTO;
import com.postfinance.cryptowallet.dto.WalletPerformanceDTO;
import com.postfinance.cryptowallet.mapper.WalletMapper;
import com.postfinance.cryptowallet.model.Asset;
import com.postfinance.cryptowallet.model.Wallet;
import com.postfinance.cryptowallet.repository.AssetRepository;
import com.postfinance.cryptowallet.repository.PerformanceRepository;
import com.postfinance.cryptowallet.repository.WalletRepository;
import com.postfinance.cryptowallet.util.AssetCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;

class WalletServiceTest {

    private static final String BTC = "BTC";
    private static final String BITCOIN_ID = "bitcoin-id";

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CoincapService coincapService;

    @Mock
    private WalletHistoryService walletHistoryService;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private AssetCache assetCache;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;
    private Asset asset;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        asset = new Asset();
        asset.setSymbol(BTC);
        asset.setPrice(BigDecimal.valueOf(40000));
        asset.setExternalId(BITCOIN_ID);
        asset.setInitialPrice(BigDecimal.valueOf(30000));
        asset.setQuantity(BigDecimal.valueOf(1));

        List<Asset> assets = new ArrayList<>();
        assets.add(asset);

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setAssets(assets);
    }

    @Test
    void testCreateWallet() {
        when(assetCache.getIdBySymbol(BTC)).thenReturn(BITCOIN_ID);
        when(assetCache.getNameBySymbol(BTC)).thenReturn("Bitcoin");
        when(coincapService.getLatestPrice(BITCOIN_ID)).thenReturn(40000.0);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);
        when(walletMapper.toWalletDTO(any(Wallet.class))).thenReturn(new WalletDTO());

        WalletDTO result = walletService.createWallet(wallet);

        verify(walletRepository, times(1)).save(wallet);
        assertNotNull(result);
    }

    @Test
    void testGetWalletDetails() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(walletMapper.toWalletDTO(any(Wallet.class))).thenReturn(new WalletDTO());

        WalletDTO result = walletService.getWalletDetails(1L);

        verify(walletRepository, times(1)).findById(1L);
        assertNotNull(result);
    }

    @Test
    void testGetWalletDetailsThrowsException() {
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> walletService.getWalletDetails(1L));
        assertEquals("Wallet not found", exception.getMessage());
    }

    @Test
    void testDeleteWallet() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        walletService.deleteWallet(1L);

        verify(walletRepository, times(1)).delete(wallet);
        verify(assetRepository, times(1)).delete(asset);
    }

    @Test
    void testUpdateWalletData() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));
        when(coincapService.getLatestPrice(BITCOIN_ID)).thenReturn(42000.0);
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        doNothing().when(walletHistoryService).saveWalletHistory(anyLong(), any(BigDecimal.class));

        CompletableFuture<WalletPerformanceDTO> future = walletService.updateWalletData(1L);

        WalletPerformanceDTO result = future.join();

        verify(walletRepository, times(2)).findById(1L);
        assertNotNull(result);
        assertEquals(42000.00, result.getTotal().setScale(2, RoundingMode.HALF_UP).doubleValue(), 0.01);

    }

}
