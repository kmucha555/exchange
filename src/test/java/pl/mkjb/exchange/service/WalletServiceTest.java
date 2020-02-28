package pl.mkjb.exchange.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.WalletModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@Slf4j
class WalletServiceTest {
    private final CurrencyService currencyServiceMock = Mockito.mock(CurrencyService.class);
    private final TransactionRepository transactionRepositoryMock = Mockito.mock(TransactionRepository.class);
    private final WalletService walletService = new WalletService(currencyServiceMock, transactionRepositoryMock);
    private CurrencyRateEntity currencyRateEntity;
    private CurrencyRateEntity baseCurrencyRateEntity;
    private UUID currencyRateId;
    private UUID baseCurrencyRateId;

    @BeforeEach
    void init() {
        currencyRateId = UUID.randomUUID();
        baseCurrencyRateId = UUID.randomUUID();
        val currencyId = 1;
        val sellPrice = BigDecimal.valueOf(3.7392);
        val purchasePrice = BigDecimal.valueOf(3.7222);
        val averagePrice = BigDecimal.valueOf(3.7300);
        val publicationDate = LocalDateTime.of(2020, 2, 26, 17, 20, 5);
        val createdAt = LocalDateTime.of(2020, 2, 26, 17, 20, 20);
        val currencyEntity = new CurrencyEntity(currencyId, "US Dollar", "USD", 1, false);
        val baseCurrencyEntity = new CurrencyEntity(currencyId, "Polish zloty", "PLN", 1, true);

        currencyRateEntity =
                new CurrencyRateEntity(
                        currencyRateId,
                        currencyEntity,
                        purchasePrice,
                        sellPrice,
                        averagePrice,
                        publicationDate,
                        Boolean.TRUE,
                        createdAt);

        baseCurrencyRateEntity =
                new CurrencyRateEntity(
                        baseCurrencyRateId,
                        baseCurrencyEntity,
                        BigDecimal.ONE,
                        BigDecimal.ONE,
                        BigDecimal.ONE,
                        publicationDate,
                        Boolean.TRUE,
                        createdAt);
    }


    @Test
    void givenZeroInUserWalletForBaseCurrency_whenTestingUserHasInsufficientFunds_thenReturnTrue() {
        //given
        val userId = 1L;
        val userWalletCurrencyAmount = BigDecimal.ZERO;
        val userWallet = Set.of(new WalletModel(baseCurrencyRateId, "PLN", 1, userWalletCurrencyAmount, BigDecimal.ONE));
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntity);
        when(currencyServiceMock.findBaseCurrencyRate()).thenReturn(baseCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userId)).thenReturn(userWallet);

        //when
        final boolean test = walletService.hasInsufficientFundsForBuyCurrency(currencyRateId, userId);

        //then
        assertThat(test, equalTo(true));
    }

    @Test
    void givenGreaterThanZeroInUserWalletForBaseCurrency_whenTestingUserHasInsufficientFunds_thenReturnFalse() {
        //given
        val userId = 1L;
        val userWalletCurrencyAmount = BigDecimal.TEN;
        val userWallet = Set.of(new WalletModel(baseCurrencyRateId, "PLN", 1, userWalletCurrencyAmount, BigDecimal.ONE));
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntity);
        when(currencyServiceMock.findBaseCurrencyRate()).thenReturn(baseCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userId)).thenReturn(userWallet);

        //when
        final boolean test = walletService.hasInsufficientFundsForBuyCurrency(currencyRateId, userId);

        //then
        assertThat(test, equalTo(false));
    }

    @Test
    void givenGreaterThanZeroInUserWalletForBaseCurrency_whenGetWalletAmountForBaseCurrency_thenReturnInputAmount() {
        //given
        val userId = 1L;
        val userWalletCurrencyAmount = BigDecimal.TEN;
        val userWallet = Set.of(new WalletModel(baseCurrencyRateId, "PLN", 1, userWalletCurrencyAmount, BigDecimal.ONE));
        when(currencyServiceMock.findBaseCurrencyRate()).thenReturn(baseCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userId)).thenReturn(userWallet);

        //when
        BigDecimal test = walletService.getUserWalletAmountForBaseCurrency(userId);

        //then
        assertThat(test, equalTo(BigDecimal.TEN));
    }
}