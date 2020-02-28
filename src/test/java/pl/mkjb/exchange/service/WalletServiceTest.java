package pl.mkjb.exchange.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.model.WalletModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

class WalletServiceTest {
    private CurrencyService currencyServiceMock = Mockito.mock(CurrencyService.class);
    private TransactionRepository transactionRepositoryMock = Mockito.mock(TransactionRepository.class);
    private WalletService walletService = new WalletService(currencyServiceMock, transactionRepositoryMock);
    private CurrencyEntity currencyEntity;
    private CurrencyEntity billingCurrencyEntity;
    private CurrencyRateEntity currencyRateEntity;
    private CurrencyRateEntity billingCurrencyRateEntity;
    private UUID currencyRateId;
    private UUID billingCurrencyRateId;
    private BigDecimal currencySellPrice;
    private BigDecimal currencyPurchasePrice;
    private BigDecimal currencyAveragePrice;
    private BigDecimal billingCurrencyPurchasePrice;
    private long userId;
    private LocalDateTime publicationDate;
    private LocalDateTime createdAt;

    @BeforeEach
    void init() {
        userId = 1;
        currencyRateId = UUID.randomUUID();
        billingCurrencyRateId = UUID.randomUUID();
        publicationDate = LocalDateTime.of(2020, 2, 26, 17, 20, 5);
        createdAt = LocalDateTime.of(2020, 2, 26, 17, 20, 20);
        currencyEntity = new CurrencyEntity(1, "US Dollar", "USD", BigDecimal.ONE, false);
        billingCurrencyEntity = new CurrencyEntity(2, "Polish zloty", "PLN", BigDecimal.ONE, true);
        currencySellPrice = BigDecimal.valueOf(3.7392);
        currencyPurchasePrice = BigDecimal.valueOf(3.7222);
        currencyAveragePrice = BigDecimal.valueOf(3.7300);
        billingCurrencyPurchasePrice = BigDecimal.ONE;

        currencyRateEntity =
                new CurrencyRateEntity(
                        currencyRateId,
                        currencyEntity,
                        currencyPurchasePrice,
                        currencySellPrice,
                        currencyAveragePrice,
                        publicationDate,
                        Boolean.TRUE,
                        createdAt);

        billingCurrencyRateEntity =
                new CurrencyRateEntity(
                        billingCurrencyRateId,
                        billingCurrencyEntity,
                        BigDecimal.ONE,
                        BigDecimal.ONE,
                        BigDecimal.ONE,
                        publicationDate,
                        Boolean.TRUE,
                        createdAt);
    }


    @Test
    void givenZeroInUserWalletForBillingCurrency_whenTestingUserHasInsufficientFunds_thenReturnTrue() {
        //given
        var userWalletCurrencyAmount = BigDecimal.ZERO;
        var userWallet = Set.of(new WalletModel(billingCurrencyRateId, "PLN", BigDecimal.ONE, userWalletCurrencyAmount, BigDecimal.ONE));
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntity);
        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userId)).thenReturn(userWallet);

        //when
        boolean test = walletService.hasInsufficientFundsForBuyCurrency(currencyRateId, userId);

        //then
        assertThat(test, equalTo(true));
    }

    @Test
    void givenGreaterThanZeroInUserWalletForBillingCurrency_whenTestingUserHasInsufficientFunds_thenReturnFalse() {
        //given
        var userWalletCurrencyAmount = BigDecimal.TEN;
        var userWallet = Set.of(new WalletModel(billingCurrencyRateId, "PLN", BigDecimal.ONE, userWalletCurrencyAmount, billingCurrencyPurchasePrice));
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntity);
        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userId)).thenReturn(userWallet);

        //when
        boolean test = walletService.hasInsufficientFundsForBuyCurrency(currencyRateId, userId);

        //then
        assertThat(test, equalTo(false));
    }

    @Test
    void givenGreaterThanZeroAmountInUserWalletForBillingCurrency_whenGetWalletAmountForBillingCurrency_thenReturnInputAmount() {
        //given
        var userWalletCurrencyAmount = BigDecimal.TEN;
        var userWallet = Set.of(new WalletModel(billingCurrencyRateId, "PLN", BigDecimal.ONE, userWalletCurrencyAmount, billingCurrencyPurchasePrice));
        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userId)).thenReturn(userWallet);

        //when
        BigDecimal test = walletService.getUserWalletAmountForBillingCurrency(userId);

        //then
        assertThat(test, equalTo(BigDecimal.TEN));
    }

    @Test
    void givenGreaterThanZeroAmountInUserWalletForCurrency_whenGetWalletAmountForCurrency_thenReturnInputAmount() {
        //given
        var userWalletCurrencyAmount = BigDecimal.TEN;
        var userWallet = Set.of(new WalletModel(currencyRateId, "USD", BigDecimal.ONE, userWalletCurrencyAmount, currencyPurchasePrice));
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userId)).thenReturn(userWallet);

        //when
        BigDecimal test = walletService.getUserWalletAmountForGivenCurrency(currencyRateId, userId);

        //then
        assertThat(test, equalTo(BigDecimal.TEN));
    }

    @Test
    void givenMockedUserWallet_whenGetUserWalletIsCalled_thenReturnUserWallet() {
        //given
        var userWalletCurrencyAmountUSD = BigDecimal.valueOf(1000);

        var currencyRateEntityCZK =
                new CurrencyRateEntity(
                        currencyRateId,
                        currencyEntity,
                        currencyPurchasePrice,
                        currencySellPrice,
                        currencyAveragePrice,
                        publicationDate,
                        Boolean.TRUE,
                        createdAt);

        var currencyModels = Set.of(
                CurrencyModel.buildCurrencyModel(currencyRateEntityCZK));
        var currencyRatesModel = CurrencyRatesModel.of(publicationDate, currencyModels);
        var userWallet = Set.of(
                new WalletModel(currencyRateId, "USD", BigDecimal.ONE, userWalletCurrencyAmountUSD, currencyPurchasePrice)
        );

        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(currencyServiceMock.getNewestRates()).thenReturn(currencyRatesModel);
        when(transactionRepositoryMock.findUserWallet(userId)).thenReturn(userWallet);

        //when
        Set<WalletModel> test = walletService.getUserWallet(userId);

        //then
        assertThat(test, equalTo(userWallet));
    }
}