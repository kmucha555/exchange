package pl.mkjb.exchange.service;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.model.UserWalletModel;
import pl.mkjb.exchange.repository.TransactionRepository;
import pl.mkjb.exchange.security.CustomUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private UserDetails userDetails;
    private UUID currencyRateId;
    private UUID billingCurrencyRateId;
    private BigDecimal currencySellPrice;
    private BigDecimal currencyPurchasePrice;
    private BigDecimal currencyAveragePrice;
    private BigDecimal billingCurrencyPurchasePrice;
    private LocalDateTime publicationDate;
    private LocalDateTime createdAt;

    @BeforeEach
    void init() {
        currencyRateId = UUID.randomUUID();
        billingCurrencyRateId = UUID.randomUUID();
        publicationDate = LocalDateTime.of(2020, 2, 26, 17, 20, 5);
        createdAt = LocalDateTime.of(2020, 2, 26, 17, 20, 20);
        currencySellPrice = BigDecimal.valueOf(3.7392);
        currencyPurchasePrice = BigDecimal.valueOf(3.7222);
        currencyAveragePrice = BigDecimal.valueOf(3.7300);
        billingCurrencyPurchasePrice = BigDecimal.ONE;

        currencyEntity = CurrencyEntity.builder()
                .id(1)
                .name("US Dollar")
                .code("USD")
                .unit(BigDecimal.ONE)
                .billingCurrency(false)
                .build();

        billingCurrencyEntity = CurrencyEntity.builder()
                .id(1)
                .name("Polish zloty")
                .code("PLN")
                .unit(BigDecimal.ONE)
                .billingCurrency(true)
                .build();

        currencyRateEntity = CurrencyRateEntity.builder()
                .id(currencyRateId)
                .currencyEntity(currencyEntity)
                .purchasePrice(currencyPurchasePrice)
                .sellPrice(currencySellPrice)
                .averagePrice(currencyAveragePrice)
                .publicationDate(publicationDate)
                .active(Boolean.TRUE)
                .createdAt(createdAt)
                .build();

        billingCurrencyRateEntity = CurrencyRateEntity.builder()
                .id(billingCurrencyRateId)
                .currencyEntity(billingCurrencyEntity)
                .purchasePrice(BigDecimal.ONE)
                .sellPrice(BigDecimal.ONE)
                .averagePrice(BigDecimal.ONE)
                .publicationDate(publicationDate)
                .active(Boolean.TRUE)
                .createdAt(createdAt)
                .build();

        userDetails = CustomUser.buildCustomUser()
                .id(1L)
                .username("test-user")
                .fullName("Test User")
                .password("Password")
                .enabled(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .authorities(java.util.Set.of())
                .build();
    }

    @Test
    void givenZeroInUserWalletForBillingCurrency_whenTestingUserHasInsufficientFunds_thenReturnTrue() {
        //given
        var userWalletCurrencyAmount = BigDecimal.ZERO;
        var userWallet = HashSet.of(new UserWalletModel(billingCurrencyRateId, "PLN", BigDecimal.ONE, userWalletCurrencyAmount, BigDecimal.ONE));
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntity);
        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userDetails.getUsername())).thenReturn(userWallet);

        //when
        boolean test = walletService.hasInsufficientFundsForBuyCurrency(currencyRateId, userDetails);

        //then
        assertThat(test, equalTo(true));
    }

    @Test
    void givenGreaterThanZeroInUserWalletForBillingCurrency_whenTestingUserHasInsufficientFunds_thenReturnFalse() {
        //given
        var userWalletCurrencyAmount = BigDecimal.TEN;
        var userWallet = HashSet.of(new UserWalletModel(billingCurrencyRateId, "PLN", BigDecimal.ONE, userWalletCurrencyAmount, billingCurrencyPurchasePrice));
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntity);
        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userDetails.getUsername())).thenReturn(userWallet);

        //when
        boolean test = walletService.hasInsufficientFundsForBuyCurrency(currencyRateId, userDetails);

        //then
        assertThat(test, equalTo(false));
    }

    @Test
    void givenGreaterThanZeroAmountInUserWalletForBillingCurrency_whenGetWalletAmountForBillingCurrency_thenReturnInputAmount() {
        //given
        var userWalletCurrencyAmount = BigDecimal.TEN;
        var userWallet = HashSet.of(new UserWalletModel(billingCurrencyRateId, "PLN", BigDecimal.ONE, userWalletCurrencyAmount, billingCurrencyPurchasePrice));
        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userDetails.getUsername())).thenReturn(userWallet);

        //when
        BigDecimal test = walletService.getUserWalletAmountForBillingCurrency(userDetails);

        //then
        assertThat(test, equalTo(BigDecimal.TEN));
    }

    @Test
    void givenGreaterThanZeroAmountInUserWalletForCurrency_whenGetWalletAmountForCurrency_thenReturnInputAmount() {
        //given
        var userWalletCurrencyAmount = BigDecimal.TEN;
        var userWallet = HashSet.of(new UserWalletModel(currencyRateId, "USD", BigDecimal.ONE, userWalletCurrencyAmount, currencyPurchasePrice));
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntity);
        when(transactionRepositoryMock.findUserWallet(userDetails.getUsername())).thenReturn(userWallet);

        //when
        BigDecimal test = walletService.getUserWalletAmountForGivenCurrency(currencyRateId, userDetails);

        //then
        assertThat(test, equalTo(BigDecimal.TEN));
    }

    @Test
    void givenMockedUserWallet_whenGetUserWalletIsCalled_thenReturnUserWallet() {
        //given
        var userWalletCurrencyAmountUSD = BigDecimal.valueOf(1000);

        var currencyRateEntityCZK = CurrencyRateEntity.builder()
                .id(currencyRateId)
                .currencyEntity(currencyEntity)
                .purchasePrice(currencyPurchasePrice)
                .sellPrice(currencySellPrice)
                .averagePrice(currencyAveragePrice)
                .publicationDate(publicationDate)
                .active(Boolean.TRUE)
                .createdAt(createdAt)
                .build();

        var currencyModels = java.util.Set.of(
                CurrencyModel.buildCurrencyModel(currencyRateEntityCZK)
        );

        var currencyRatesModel = CurrencyRatesModel.of(publicationDate, currencyModels);

        var userWallet = HashSet.of(
                new UserWalletModel(currencyRateId, "USD", BigDecimal.ONE, userWalletCurrencyAmountUSD, currencyPurchasePrice)
        );

        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(currencyServiceMock.getNewestRates()).thenReturn(currencyRatesModel);
        when(transactionRepositoryMock.findUserWallet(userDetails.getUsername())).thenReturn(userWallet);

        //when
        Set<UserWalletModel> test = walletService.getUserWallet(userDetails);

        //then
        assertThat(test, equalTo(userWallet));
    }
}