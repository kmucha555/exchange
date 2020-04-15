//package pl.mkjb.exchange.restclient;
//
//import lombok.extern.slf4j.Slf4j;
//import lombok.val;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import pl.mkjb.exchange.currency.domain.CurrencyEntity;
//import pl.mkjb.exchange.currency.domain.CurrencyRateEntity;
//import pl.mkjb.exchange.restclient.dto.CurrencyDto;
//import pl.mkjb.exchange.restclient.dto.CurrencyBundle;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Set;
//import java.util.UUID;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.core.IsEqual.equalTo;
//
//@Slf4j
//class FutureProcessingRestClientTest {
//    private RestTemplate restTemplateMock = Mockito.mock(RestTemplate.class);
//    private FutureProcessingRestClient futureProcessingRestClient = new FutureProcessingRestClient(restTemplateMock);
//
//    @Value("${pl.mkjb.exchange.restclient.api.url}")
//    private String currencyRatesUrl;
//
//    @Test
//    void shouldReturnMockedObjectWhenGetForEntityIsCalled() {
//        //given
//        val userWalletCurrencyAmountUSD = BigDecimal.ONE;
//        val userWalletCurrencyPurchasePriceUSD = BigDecimal.valueOf(3.7222);
//        val publicationDate = LocalDateTime.of(2020, 2, 26, 17, 20, 5);
//        val createdAt = LocalDateTime.of(2020, 2, 26, 17, 20, 20);
//        val currencyRateEntityUSD =
//                new CurrencyRateEntity(
//                        UUID.randomUUID(),
//                        new CurrencyEntity(1, "US Dollar", "USD", BigDecimal.ONE, false),
//                        userWalletCurrencyAmountUSD,
//                        BigDecimal.ONE,
//                        userWalletCurrencyPurchasePriceUSD,
//                        publicationDate,
//                        Boolean.TRUE,
//                        createdAt);
//
//        val currencyModels = Set.of(CurrencyDto.buildCurrencyModel(currencyRateEntityUSD));
//        val currencyRatesModel = CurrencyBundle.of(publicationDate, currencyModels);
//
//        Mockito.when(restTemplateMock.getForEntity(currencyRatesUrl, CurrencyBundle.class))
//                .thenReturn(ResponseEntity.status(HttpStatus.OK).body(currencyRatesModel));
//
//        //when
//        final CurrencyBundle test = futureProcessingRestClient.getCurrenciesRates().get();
//
//        //then
//        assertThat(test, equalTo(currencyRatesModel));
//    }
//}