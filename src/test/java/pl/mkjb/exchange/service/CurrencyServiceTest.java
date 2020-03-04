package pl.mkjb.exchange.service;

import org.junit.jupiter.api.Test;
import pl.mkjb.exchange.entity.CurrencyEntity;
import pl.mkjb.exchange.exception.BadResourceException;
import pl.mkjb.exchange.repository.CurrencyRateRepository;
import pl.mkjb.exchange.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyServiceTest {
    private final CurrencyRepository currencyRepositoryMock = mock(CurrencyRepository.class);
    private final CurrencyRateRepository currencyRateRepositoryMock = mock(CurrencyRateRepository.class);
    private final CurrencyService currencyService = new CurrencyService(currencyRepositoryMock, currencyRateRepositoryMock);

    @Test
    void shouldThrowBadResourceExceptionWhenCurrencyNotFound() {
        //given
        final int existingCurrencyId = 1;
        final int notExistingCurrencyId = 0;
        when(currencyRepositoryMock.findById(existingCurrencyId)).thenReturn(Optional.of(new CurrencyEntity()));

        //when - then
        assertThrows(BadResourceException.class, () -> currencyService.findCurrencyById(notExistingCurrencyId));
    }

    @Test
    void shouldReturnCurrencyEntityWhenGiven() {
        //given
        final int currencyId = 1;
        final CurrencyEntity currencyEntity = CurrencyEntity.builder()
                .id(currencyId)
                .code("USD")
                .name("US Dollar")
                .unit(BigDecimal.ONE)
                .billingCurrency(Boolean.FALSE)
                .build();
        when(currencyRepositoryMock.findById(currencyId)).thenReturn(Optional.of(currencyEntity));

        //when
        final CurrencyEntity test = currencyService.findCurrencyById(currencyId);

        //when - then
        assertThat(test, equalTo(currencyEntity));
    }
}