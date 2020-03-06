//package pl.mkjb.exchange.validator;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import pl.mkjb.exchange.entity.CurrencyEntity;
//import pl.mkjb.exchange.entity.CurrencyRateEntity;
//import pl.mkjb.exchange.model.TransactionModel;
//import pl.mkjb.exchange.security.CustomUser;
//import pl.mkjb.exchange.service.CurrencyService;
//import pl.mkjb.exchange.service.Transaction;
//import pl.mkjb.exchange.service.TransactionBuyService;
//import pl.mkjb.exchange.service.TransactionFacadeService;
//
//import javax.validation.ConstraintValidatorContext;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Set;
//import java.util.UUID;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.core.IsEqual.equalTo;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static pl.mkjb.exchange.util.TransactionTypeConstant.BUY;
//
//class TransactionAmountValidatorTest {
//    private String transactionAmountFieldName;
//    private TransactionModel transactionModelUSD;
//    private TransactionModel transactionModelCZK;
//    private CurrencyRateEntity currencyRateEntity;
//    private CurrencyEntity currencyEntityUSD;
//    private CurrencyEntity currencyEntityCZK;
//    private CustomUser userDetails;
//    private final Transaction transactionBuyServiceMock = mock(TransactionBuyService.class);
//    private final CurrencyService currencyServiceMock = mock(CurrencyService.class);
//    private final TransactionFacadeService transactionFacadeServiceMock = mock(TransactionFacadeService.class);
//    private final ConstraintValidatorContext contextMock = mock(ConstraintValidatorContext.class);
//    private final TransactionAmountValidator transactionAmountValidator =
//            new TransactionAmountValidator(currencyServiceMock, transactionFacadeServiceMock);
//
//    @BeforeEach
//    void init() {
//        var currencyUnit = BigDecimal.ONE;
//        var transactionPriceUSD = BigDecimal.valueOf(3.3434);
//        var transactionPriceCZK = BigDecimal.valueOf(14.3434);
//        var userWalletAmount = BigDecimal.valueOf(100);
//        var maxAllowedTransactionAmount = BigDecimal.valueOf(1000);
//        var currencyRateId = UUID.randomUUID();
//
//        transactionModelUSD = TransactionModel.builder()
//                .currencyRateId(currencyRateId)
//                .currencyCode("USD")
//                .currencyUnit(currencyUnit)
//                .transactionPrice(transactionPriceUSD)
//                .userWalletAmount(userWalletAmount)
//                .maxAllowedTransactionAmount(maxAllowedTransactionAmount)
//                .transactionTypeConstant(BUY)
//                .build();
//
//        transactionModelUSD = TransactionModel.builder()
//                .currencyRateId(currencyRateId)
//                .currencyCode("CZK")
//                .currencyUnit(currencyUnit)
//                .transactionPrice(transactionPriceCZK)
//                .userWalletAmount(userWalletAmount)
//                .maxAllowedTransactionAmount(maxAllowedTransactionAmount)
//                .transactionTypeConstant(BUY)
//                .build();
//
//        currencyEntityUSD = CurrencyEntity.builder()
//                .id(1)
//                .name("US Dollar")
//                .code("USD")
//                .unit(BigDecimal.ONE)
//                .billingCurrency(false)
//                .build();
//
//        currencyEntityCZK = CurrencyEntity.builder()
//                .id(1)
//                .name("Czech koruna")
//                .code("CZK")
//                .unit(BigDecimal.valueOf(100))
//                .billingCurrency(false)
//                .build();
//
//        currencyRateEntity = CurrencyRateEntity.builder()
//                .id(currencyRateId)
//                .purchasePrice(transactionPriceUSD)
//                .sellPrice(transactionPriceUSD)
//                .averagePrice(transactionPriceUSD)
//                .publicationDate(LocalDateTime.now())
//                .active(Boolean.TRUE)
//                .createdAt(LocalDateTime.now())
//                .build();
//
//        userDetails = CustomUser.buildCustomUser()
//                .id(1)
//                .username("Test")
//                .fullName("Test User")
//                .accountNonExpired(true)
//                .accountNonLocked(true)
//                .credentialsNonExpired(true)
//                .enabled(true)
//                .password("password")
//                .authorities(Set.of())
//                .build();
//    }
//
//    @Test
//    void shouldPassIfTransactionAmountEqualsZero() {
//        //given
//        var maxTransactionAmount = BigDecimal.valueOf(1000);
//        currencyRateEntity.setCurrencyEntity(currencyEntityUSD);
//        transactionModelUSD.setTransactionAmount(BigDecimal.ZERO);
//        when(transactionBuyServiceMock.estimateMaxAllowedTransactionAmountForUser(currencyRateEntity, userDetails)).thenReturn(maxTransactionAmount);
//        when(currencyServiceMock.findCurrencyRateByCurrencyRateId(transactionModelUSD.getCurrencyRateId())).thenReturn(currencyRateEntity);
//        when(transactionFacadeServiceMock.estimateMaxTransactionAmount()
//                .apply(transactionModelUSD.getTransactionTypeConstant())
//                .apply(currencyRateEntity, userDetails))
//                .thenReturn(maxTransactionAmount);
//
//        //when
//        final boolean test = transactionAmountValidator.isValid(transactionModelUSD, contextMock);
//
//        //then
//        assertThat(test, equalTo(false));
//    }
//}