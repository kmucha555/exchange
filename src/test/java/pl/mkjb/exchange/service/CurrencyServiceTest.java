package pl.mkjb.exchange.service;

import io.vavr.collection.HashSet;
import io.vavr.control.Option;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.CurrencyRatesModel;
import pl.mkjb.exchange.repository.CurrencyRateRepository;
import pl.mkjb.exchange.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyServiceTest {
    private final CurrencyRepository currencyRepositoryMock = mock(CurrencyRepository.class);
    private final CurrencyRateRepository currencyRateRepositoryMock = mock(CurrencyRateRepository.class);
    private final CurrencyService currencyService = new CurrencyService(currencyRepositoryMock, currencyRateRepositoryMock);
    private CurrencyRateEntity currencyRateEntity;
    private CurrencyEntity currencyEntity;
    private LocalDateTime publicationDate;
    private int currencyId;
    private int notExistingCurrencyId;
    private UUID currencyRateId;
    private UUID notExistingCurrencyRateId;

    @BeforeEach
    void init() {
        currencyId = 1;
        notExistingCurrencyId = 0;
        currencyRateId = UUID.randomUUID();
        notExistingCurrencyRateId = UUID.randomUUID();
        publicationDate = LocalDateTime.of(2020, 2, 26, 17, 20, 5);
        LocalDateTime createdAt = LocalDateTime.of(2020, 2, 26, 17, 20, 20);
        BigDecimal currencySellPrice = BigDecimal.valueOf(3.7392);
        BigDecimal currencyPurchasePrice = BigDecimal.valueOf(3.7222);
        BigDecimal currencyAveragePrice = BigDecimal.valueOf(3.7300);

        currencyEntity = CurrencyEntity.builder()
                .id(1)
                .name("US Dollar")
                .code("USD")
                .unit(BigDecimal.ONE)
                .billingCurrency(false)
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
    }

    @Test
    void shouldReturnCurrencyRatesModeWhenNewCurrencyRatesAvailable() {
        //given
        when(currencyRateRepositoryMock.findByActiveTrue()).thenReturn(HashSet.of(currencyRateEntity));
        val currencyRatesModel = CurrencyRatesModel.of(
                publicationDate,
                Set.of(CurrencyModel.buildCurrencyModel(currencyRateEntity)));

        //when
        final CurrencyRatesModel test = currencyService.getNewestRates();

        //when - then
        assertThat(test, equalTo(currencyRatesModel));
    }

    @Test
    void shouldThrowBadResourceExceptionWhenNoNewCurrencyRatesFound() {
        //given
        when(currencyRateRepositoryMock.findByActiveTrue()).thenReturn(HashSet.empty());

        //when - then
        assertThrows(BadResourceException.class, () -> currencyService.getNewestRates());
    }

    @Test
    void shouldThrowBadResourceExceptionWhenCurrencyNotFound() {
        //given
        when(currencyRepositoryMock.findById(currencyId)).thenReturn(Optional.of(currencyEntity));

        //when - then
        assertThrows(BadResourceException.class, () -> currencyService.findCurrencyById(notExistingCurrencyId));
    }

    @Test
    void shouldReturnCurrencyEntityWhenGivenValidCurrencyId() {
        //given
        when(currencyRepositoryMock.findById(currencyId)).thenReturn(Optional.of(currencyEntity));

        //when
        final CurrencyEntity test = currencyService.findCurrencyById(currencyId);

        //when - then
        assertThat(test, equalTo(currencyEntity));
    }

    @Test
    void shouldThrowBadResourceExceptionWhenBillingCurrencyRateNotFound() {
        //given
        when(currencyRateRepositoryMock.findByCurrencyEntityBillingCurrencyIsTrue()).thenReturn(Option.of(null));

        //when - then
        assertThrows(BadResourceException.class, () -> currencyService.findBillingCurrencyRate());
    }

    @Test
    void shouldThrowBadResourceExceptionWhenCurrencyRateNotFound() {
        //given
        when(currencyRateRepositoryMock.findById(currencyRateId)).thenReturn(Optional.of(currencyRateEntity));

        //when - then
        assertThrows(BadResourceException.class, () -> currencyService.findCurrencyRateByCurrencyRateId(notExistingCurrencyRateId));
    }

    @Test
    void shouldReturnCurrencyRateEntityWhenGivenValidCurrencyRateId() {
        //given
        when(currencyRateRepositoryMock.findById(currencyRateId)).thenReturn(Optional.of(currencyRateEntity));

        //when
        final CurrencyRateEntity test = currencyService.findCurrencyRateByCurrencyRateId(currencyRateId);

        //when - then
        assertThat(test, equalTo(currencyRateEntity));
    }

    @Test
    void shouldThrowBadResourceExceptionInvokeIsArchivedCurrencyRateWhenCurrencyRateNotFound() {
        //given
        when(currencyRateRepositoryMock.findById(currencyRateId)).thenReturn(Optional.of(currencyRateEntity));

        //when - then
        assertThrows(BadResourceException.class, () -> currencyService.findCurrencyRateByCurrencyRateId(notExistingCurrencyRateId));
    }

    @Test
    void shouldReturnTrueWhenGivenCurrencyRateIdIsArchived() {
        //given
        var archivedCurrencyRateEntity = CurrencyRateEntity.builder()
                .id(currencyRateId)
                .currencyEntity(currencyEntity)
                .publicationDate(publicationDate)
                .active(Boolean.FALSE)
                .build();

        when(currencyRateRepositoryMock.findById(currencyRateId)).thenReturn(Optional.of(archivedCurrencyRateEntity));

        //when
        final boolean test = currencyService.isArchivedCurrencyRate(currencyRateId);

        //when - then
        assertTrue(test);
    }

    @Test
    void shouldReturnFalseWhenGivenCurrencyRateIdIsActive() {
        //given
        when(currencyRateRepositoryMock.findById(currencyRateId)).thenReturn(Optional.of(currencyRateEntity));

        //when
        final boolean test = currencyService.isArchivedCurrencyRate(currencyRateId);

        //when - then
        assertFalse(test);
    }
}