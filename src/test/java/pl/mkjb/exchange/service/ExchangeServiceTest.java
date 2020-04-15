//package pl.mkjb.exchange.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import pl.mkjb.exchange.currency.domain.CurrencyEntity;
//import pl.mkjb.exchange.currency.domain.CurrencyService;
//import pl.mkjb.exchange.currency.domain.CurrencyRateEntity;
//import pl.mkjb.exchange.transaction.domain.ExchangeService;
//import pl.mkjb.exchange.transaction.domain.TransactionEntity;
//import pl.mkjb.exchange.transaction.dto.TransactionBuilder;
//import pl.mkjb.exchange.transaction.domain.TransactionRepository;
//import pl.mkjb.exchange.infrastructure.util.RoleConstant;
//import pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant;
//import pl.mkjb.exchange.user.domain.RoleEntity;
//import pl.mkjb.exchange.user.domain.UserEntity;
//import pl.mkjb.exchange.user.domain.UserService;
//
//import java.math.BigDecimal;
//import java.util.Set;
//import java.util.UUID;
//
//import static java.math.RoundingMode.HALF_UP;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.containsInAnyOrder;
//import static org.mockito.Mockito.when;
//
//@Slf4j
//class ExchangeServiceTest {
//    private TransactionRepository transactionRepositoryMock = Mockito.mock(TransactionRepository.class);
//    private UserService userServiceMock = Mockito.mock(UserService.class);
//    private CurrencyService currencyServiceMock = Mockito.mock(CurrencyService.class);
//    private ExchangeService exchangeService =
//            new ExchangeService(transactionRepositoryMock, userServiceMock, currencyServiceMock);
//
//    @Test
//    void givenTransactionBuilderMock_whenPrepareTransactionToSaveCalled_thenReturnTransactionEntitySet() {
//        //given
//        int currencyId = 2;
//        var transactionAmount = BigDecimal.TEN;
//        var currencyPurchasePrice = BigDecimal.valueOf(3.7222);
//        var username = "test-user";
//
//        var transactionCurrencyEntity = CurrencyEntity.builder()
//                .id(currencyId)
//                .name("US Dollar")
//                .code("USD")
//                .unit(BigDecimal.ONE)
//                .billingCurrency(false)
//                .build();
//
//        var billingCurrencyEntity = CurrencyEntity.builder()
//                .id(1)
//                .name("Polish zloty")
//                .code("PLN")
//                .unit(BigDecimal.ONE)
//                .billingCurrency(true)
//                .build();
//
//        var currencyRateEntity = CurrencyRateEntity.builder()
//                .id(UUID.randomUUID())
//                .currencyEntity(transactionCurrencyEntity)
//                .purchasePrice(currencyPurchasePrice)
//                .sellPrice(BigDecimal.valueOf(3.7392))
//                .averagePrice(BigDecimal.valueOf(3.7300))
//                .active(Boolean.FALSE)
//                .build();
//
//        var billingCurrencyRateEntity = CurrencyRateEntity.builder()
//                .id(UUID.randomUUID())
//                .currencyEntity(billingCurrencyEntity)
//                .purchasePrice(BigDecimal.ONE)
//                .sellPrice(BigDecimal.ONE)
//                .averagePrice(BigDecimal.ONE)
//                .active(Boolean.TRUE)
//                .build();
//
//        var roleEntityUser = Set.of(new RoleEntity(1, RoleConstant.ROLE_USER.name()));
//        var roleEntityOwner = Set.of(new RoleEntity(2, RoleConstant.ROLE_OWNER.name()));
//
//        var userEntity = UserEntity.builder()
//                .id(1L)
//                .username(username)
//                .firstName("Mock")
//                .lastName("Mock")
//                .active(Boolean.TRUE)
//                .password("Mock")
//                .roles(roleEntityUser)
//                .build();
//
//        var exchangeOwnerEntity = UserEntity.builder()
//                .id(2L)
//                .username("Exchange Owner")
//                .firstName("Mock")
//                .lastName("Mock")
//                .active(Boolean.TRUE)
//                .password("Mock")
//                .roles(roleEntityOwner)
//                .build();
//
//        when(currencyServiceMock.findCurrencyById(currencyId)).thenReturn(transactionCurrencyEntity);
//        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
//        when(userServiceMock.findOwner()).thenReturn(exchangeOwnerEntity);
//        when(userServiceMock.findByUsername(username)).thenReturn(userEntity);
//
//        var transactionBuilder = TransactionBuilder.builder()
//                .currencyRateEntity(currencyRateEntity)
//                .transactionAmount(transactionAmount)
//                .transactionPrice(currencyPurchasePrice)
//                .transactionTypeConstant(TransactionTypeConstant.SELL)
//                .userEntity(userEntity)
//                .build();
//
//        var transactionBillingCurrencyAmount = transactionBuilder.getTransactionAmount().multiply(transactionBuilder.getTransactionPrice())
//                .divide(transactionCurrencyEntity.getUnit(), HALF_UP);
//
//        var transactionOne = TransactionEntity.builder()
//                .currencyEntity(transactionBuilder.getCurrencyRateEntity().getCurrencyEntity())
//                .userEntity(userEntity)
//                .currencyRate(transactionBuilder.getTransactionPrice())
//                .amount(transactionBuilder.getTransactionAmount().negate())
//                .build();
//
//        var transactionTwo = TransactionEntity.builder()
//                .currencyEntity(transactionBuilder.getCurrencyRateEntity().getCurrencyEntity())
//                .userEntity(exchangeOwnerEntity)
//                .currencyRate(transactionBuilder.getTransactionPrice())
//                .amount(transactionBuilder.getTransactionAmount())
//                .build();
//
//        var transactionThree = TransactionEntity.builder()
//                .currencyEntity(billingCurrencyEntity)
//                .userEntity(userEntity)
//                .currencyRate(transactionBuilder.getTransactionPrice())
//                .amount(transactionBillingCurrencyAmount)
//                .build();
//
//        var transactionFour = TransactionEntity.builder()
//                .currencyEntity(billingCurrencyEntity)
//                .userEntity(exchangeOwnerEntity)
//                .currencyRate(transactionBuilder.getTransactionPrice())
//                .amount(transactionBillingCurrencyAmount.negate())
//                .build();
//
//        //when
//        final Set<TransactionEntity> test = exchangeService.prepareTransactionToSave(transactionBuilder);
//
//        //then
//        assertThat(test, containsInAnyOrder(transactionOne, transactionTwo, transactionThree, transactionFour));
//    }
//}