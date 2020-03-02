package pl.mkjb.exchange.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.mkjb.exchange.entity.*;
import pl.mkjb.exchange.model.TransactionBuilder;
import pl.mkjb.exchange.repository.TransactionRepository;
import pl.mkjb.exchange.security.CustomUser;
import pl.mkjb.exchange.util.RoleConstant;
import pl.mkjb.exchange.util.TransactionTypeConstant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static java.math.RoundingMode.HALF_UP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

@Slf4j
class ExchangeServiceTest {
    private TransactionRepository transactionRepositoryMock = Mockito.mock(TransactionRepository.class);
    private UserService userServiceMock = Mockito.mock(UserService.class);
    private CurrencyService currencyServiceMock = Mockito.mock(CurrencyService.class);
    private ExchangeService exchangeService =
            new ExchangeService(transactionRepositoryMock, userServiceMock, currencyServiceMock);

    @Test
    void givenTransactionBuilderMock_whenPrepareTransactionToSaveCalled_thenReturnTransactionEntitySet() {
        //given
        int currencyId = 1;
        long userId = 1;
        String username = "test-user";
        long ownerId = 2;
        UUID billingCurrencyRateId = UUID.randomUUID();
        UUID currencyRateId = UUID.randomUUID();
        var publicationDate = LocalDateTime.of(2020, 2, 26, 17, 20, 5);
        var createdAt = LocalDateTime.of(2020, 2, 26, 17, 20, 20);
        CurrencyEntity currencyEntity = new CurrencyEntity(1, "US Dollar", "USD", BigDecimal.ONE, false);
        CurrencyEntity billingCurrencyEntity = new CurrencyEntity(2, "Polish zloty", "PLN", BigDecimal.ONE, true);
        var currencySellPrice = BigDecimal.valueOf(3.7392);
        var currencyPurchasePrice = BigDecimal.valueOf(3.7222);
        var currencyAveragePrice = BigDecimal.valueOf(3.7300);
        var transactionAmount = BigDecimal.TEN;

        var userDetails = CustomUser.buildCustomUser()
                .id(userId)
                .username(username)
                .fullName(username)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .password(username)
                .authorities(Set.of())
                .build();

        var currencyRateEntity = new CurrencyRateEntity(
                currencyRateId,
                currencyEntity,
                currencyPurchasePrice,
                currencySellPrice,
                currencyAveragePrice,
                publicationDate,
                Boolean.FALSE,
                createdAt);

        var billingCurrencyRateEntity = new CurrencyRateEntity(
                billingCurrencyRateId,
                billingCurrencyEntity,
                BigDecimal.ONE,
                BigDecimal.ONE,
                BigDecimal.ONE,
                publicationDate,
                Boolean.TRUE,
                createdAt);

        var roleEntityUser = new RoleEntity(1, RoleConstant.ROLE_USER.name());
        var roleEntityOwner = new RoleEntity(2, RoleConstant.ROLE_OWNER.name());
        var userEntity = new UserEntity(
                1L,
                "Mock",
                "Mock",
                "Mock",
                Boolean.TRUE,
                "Mock",
                Set.of(roleEntityUser),
                createdAt);

        var exchangeOwnerEntity = new UserEntity(ownerId,
                "Mock",
                "Mock",
                "Mock",
                Boolean.TRUE,
                "Mock",
                Set.of(roleEntityOwner),
                createdAt);

        when(currencyServiceMock.findCurrencyById(currencyId)).thenReturn(currencyEntity);
        when(currencyServiceMock.findBillingCurrencyRate()).thenReturn(billingCurrencyRateEntity);
        when(userServiceMock.findOwner()).thenReturn(exchangeOwnerEntity);
        when(userServiceMock.findByUsername(username)).thenReturn(userEntity);

        var transactionBuilder = TransactionBuilder.builder()
                .currencyRateEntity(currencyRateEntity)
                .transactionAmount(transactionAmount)
                .transactionPrice(currencyPurchasePrice)
                .transactionTypeConstant(TransactionTypeConstant.SELL)
                .userDetails(userDetails)
                .build();

        var transactionBillingCurrencyAmount = transactionBuilder.getTransactionAmount().multiply(transactionBuilder.getTransactionPrice())
                .divide(currencyEntity.getUnit(), HALF_UP);

        var transactionOne = TransactionEntity.builder()
                .currencyEntity(currencyEntity)
                .userEntity(userEntity)
                .currencyRate(transactionBuilder.getTransactionPrice())
                .amount(transactionBuilder.getTransactionAmount().negate())
                .build();
        var transactionTwo = TransactionEntity.builder()
                .currencyEntity(currencyEntity)
                .userEntity(exchangeOwnerEntity)
                .currencyRate(transactionBuilder.getTransactionPrice())
                .amount(transactionBuilder.getTransactionAmount())
                .build();
        var transactionThree = TransactionEntity.builder()
                .currencyEntity(billingCurrencyEntity)
                .userEntity(userEntity)
                .currencyRate(transactionBuilder.getTransactionPrice())
                .amount(transactionBillingCurrencyAmount)
                .build();
        var transactionFour = TransactionEntity.builder()
                .currencyEntity(billingCurrencyEntity)
                .userEntity(exchangeOwnerEntity)
                .currencyRate(transactionBuilder.getTransactionPrice())
                .amount(transactionBillingCurrencyAmount.negate())
                .build();

        //when
        final Set<TransactionEntity> test = exchangeService.prepareTransactionToSave(transactionBuilder);

        //then
        assertThat(test, containsInAnyOrder(transactionOne, transactionTwo, transactionThree, transactionFour));
    }
}