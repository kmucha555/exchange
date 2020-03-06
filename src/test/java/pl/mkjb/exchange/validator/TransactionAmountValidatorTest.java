package pl.mkjb.exchange.validator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.security.CustomUser;
import pl.mkjb.exchange.service.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.mkjb.exchange.util.TransactionTypeConstant.BUY;

@Slf4j
class TransactionAmountValidatorTest {
    private TransactionModel transactionModelUSD;
    private TransactionModel transactionModelCZK;
    private CurrencyRateEntity currencyRateEntityUSD;
    private CurrencyRateEntity currencyRateEntityCZK;
    private CustomUser userDetails;
    private UUID currencyRateId;
    private BigDecimal maxAllowedTransactionAmount;
    private final Transaction transactionBuyServiceMock = mock(TransactionBuyService.class);
    private final Transaction transactionSellServiceMock = mock(TransactionSellService.class);
    private final CurrencyService currencyServiceMock = mock(CurrencyService.class);
    private final Authentication authenticationMock = mock(Authentication.class);
    private final SecurityContext securityContextMock = mock(SecurityContext.class);
    private final TransactionFacadeService transactionFacadeService = new TransactionFacadeService(transactionBuyServiceMock, transactionSellServiceMock);
    private final TransactionAmountValidator transactionAmountValidator =
            new TransactionAmountValidator(currencyServiceMock, transactionFacadeService);

    @BeforeEach
    void init() {
        var transactionPriceUSD = BigDecimal.valueOf(3.3434);
        var transactionPriceCZK = BigDecimal.valueOf(14.3434);
        maxAllowedTransactionAmount = BigDecimal.valueOf(1000);
        var userWalletAmount = BigDecimal.valueOf(100);
        currencyRateId = UUID.randomUUID();

        transactionModelUSD = TransactionModel.builder()
                .currencyRateId(currencyRateId)
                .currencyCode("USD")
                .currencyUnit(BigDecimal.ONE)
                .transactionPrice(transactionPriceUSD)
                .userWalletAmount(userWalletAmount)
                .maxAllowedTransactionAmount(maxAllowedTransactionAmount)
                .transactionTypeConstant(BUY)
                .build();

        transactionModelCZK = TransactionModel.builder()
                .currencyRateId(currencyRateId)
                .currencyCode("CZK")
                .currencyUnit(BigDecimal.valueOf(100))
                .transactionPrice(transactionPriceCZK)
                .userWalletAmount(userWalletAmount)
                .maxAllowedTransactionAmount(maxAllowedTransactionAmount)
                .transactionTypeConstant(BUY)
                .build();

        var currencyEntityUSD = CurrencyEntity.builder()
                .id(1)
                .name("US Dollar")
                .code("USD")
                .unit(BigDecimal.ONE)
                .billingCurrency(false)
                .build();

        var currencyEntityCZK = CurrencyEntity.builder()
                .id(1)
                .name("Czech koruna")
                .code("CZK")
                .unit(BigDecimal.valueOf(100))
                .billingCurrency(false)
                .build();

        currencyRateEntityUSD = CurrencyRateEntity.builder()
                .id(currencyRateId)
                .currencyEntity(currencyEntityUSD)
                .purchasePrice(transactionPriceUSD)
                .sellPrice(transactionPriceUSD)
                .averagePrice(transactionPriceUSD)
                .active(Boolean.TRUE)
                .build();

        currencyRateEntityCZK = CurrencyRateEntity.builder()
                .id(currencyRateId)
                .currencyEntity(currencyEntityCZK)
                .purchasePrice(transactionPriceCZK)
                .sellPrice(transactionPriceCZK)
                .averagePrice(transactionPriceCZK)
                .active(Boolean.TRUE)
                .build();

        userDetails = CustomUser.buildCustomUser()
                .id(1)
                .username("test-user")
                .fullName("Test User")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .password("password")
                .authorities(Set.of())
                .build();
    }

    @Test
    void shouldPassIfTransactionAmountIsLessThenZero() {
        //given
        var transactionAmount = BigDecimal.valueOf(-1);
        transactionModelUSD.setTransactionAmount(transactionAmount);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountGreaterThenZero().test(transactionModelUSD);

        //then
        assertThat(test, equalTo(false));
    }

    @Test
    void shouldPassIfTransactionAmountEqualsZero() {
        //given
        var transactionAmount = BigDecimal.ZERO;
        transactionModelUSD.setTransactionAmount(transactionAmount);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountGreaterThenZero().test(transactionModelUSD);

        //then
        assertThat(test, equalTo(false));
    }

    @Test
    void shouldPassIfTransactionAmountGreaterThanZero() {
        //given
        var transactionAmount = BigDecimal.ONE;
        transactionModelUSD.setTransactionAmount(transactionAmount);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountGreaterThenZero().test(transactionModelUSD);

        //then
        assertThat(test, equalTo(true));
    }

    @Test
    void shouldPassIfTransactionAmountDivisibleByCurrencyUnitEquals1() {
        //given
        var transactionAmount = BigDecimal.ONE;
        transactionModelUSD.setTransactionAmount(transactionAmount);
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntityUSD);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountDivisibleByCurrencyUnit().test(transactionModelUSD);

        //then
        assertThat(test, equalTo(true));
    }

    @Test
    void shouldPassIfTransactionAmountIsNotDivisibleByCurrencyUnitEquals1() {
        //given
        var transactionAmount = BigDecimal.valueOf(1.5);
        transactionModelUSD.setTransactionAmount(transactionAmount);
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntityUSD);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountDivisibleByCurrencyUnit().test(transactionModelUSD);

        //then
        assertThat(test, equalTo(false));
    }

    @Test
    void shouldPassIfTransactionAmountDivisibleByCurrencyUnitEquals100() {
        //given
        var transactionAmount = BigDecimal.valueOf(100);
        transactionModelCZK.setTransactionAmount(transactionAmount);
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntityCZK);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountDivisibleByCurrencyUnit().test(transactionModelCZK);

        //then
        assertThat(test, equalTo(true));
    }

    @Test
    void shouldPassIfTransactionAmountIsNotDivisibleByCurrencyUnitEquals100() {
        //given
        var transactionAmount = BigDecimal.valueOf(101);
        transactionModelCZK.setTransactionAmount(transactionAmount);
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntityCZK);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountDivisibleByCurrencyUnit().test(transactionModelCZK);

        //then
        assertThat(test, equalTo(false));
    }

    @Test
    void shouldPassIfTransactionAmountIsLessThenAvailableFundsInExchange() {
        //given
        var transactionAmount = BigDecimal.valueOf(100);
        transactionModelUSD.setTransactionAmount(transactionAmount);

        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        SecurityContextHolder.setContext(securityContextMock);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(userDetails);
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntityUSD);
        when(transactionAmountValidator.getCustomUser().get()).thenReturn(userDetails);
        when(transactionBuyServiceMock.estimateMaxAllowedTransactionAmountForUser(currencyRateEntityUSD, userDetails)).thenReturn(maxAllowedTransactionAmount);
        when(transactionFacadeService.estimateMaxTransactionAmount().apply(BUY).apply(currencyRateEntityUSD, userDetails)).thenReturn(maxAllowedTransactionAmount);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountLessThenFundsAvailableInExchange().test(transactionModelUSD);

        //then
        assertThat(test, equalTo(true));
    }

    @Test
    void shouldPassIfTransactionAmountIsGreaterThenAvailableFundsInExchange() {
        //given
        var transactionAmount = BigDecimal.valueOf(10000);
        transactionModelUSD.setTransactionAmount(transactionAmount);

        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        SecurityContextHolder.setContext(securityContextMock);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(userDetails);
        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(currencyRateId)).thenReturn(currencyRateEntityUSD);
        when(transactionAmountValidator.getCustomUser().get()).thenReturn(userDetails);
        when(transactionBuyServiceMock.estimateMaxAllowedTransactionAmountForUser(currencyRateEntityUSD, userDetails)).thenReturn(maxAllowedTransactionAmount);
        when(transactionFacadeService.estimateMaxTransactionAmount().apply(BUY).apply(currencyRateEntityUSD, userDetails)).thenReturn(maxAllowedTransactionAmount);

        //when
        final boolean test = transactionAmountValidator.isTransactionAmountLessThenFundsAvailableInExchange().test(transactionModelUSD);

        //then
        assertThat(test, equalTo(false));
    }
}