package pl.mkjb.exchange.currency.domain;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.CurrencyNotFoundException;
import pl.mkjb.exchange.restclient.dto.CurrencyFutureProcessingBundle;
import pl.mkjb.exchange.transaction.domain.TransactionEntity;
import pl.mkjb.exchange.user.domain.UserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CurrencyFacade {
    private final CurrencyRateCreator currencyRateCreator;
    private final CurrencyRepository currencyRepository;
    private final CurrencyRateRepository currencyRateRepository;

    public Option<CurrencyRateDto> findBillingCurrency() {
        return currencyRateRepository.findByCurrencyEntityBillingCurrencyIsTrue()
                .map(CurrencyRateEntity::toDto);
    }

    public Option<CurrencyRateDto> findCurrencyRateByCurrencyRateId(UUID id) {
        return Option.ofOptional(currencyRateRepository.findById(id))
                .map(CurrencyRateEntity::toDto);
    }

    public Set<CurrencyRateDto> getNewestCurrencyRates() {
        return currencyRateRepository.findByActiveTrue()
                .map(CurrencyRateEntity::toDto)
                .toSet();
    }

    public boolean isArchivedCurrencyRate(UUID id) {
        return !currencyRateRepository.findById(id)
                .map(CurrencyRateEntity::getActive)
                .orElseThrow(() -> new CurrencyNotFoundException(id));
    }

    public Option<Iterable<CurrencyRateEntity>> processNewCurrencyRates(CurrencyFutureProcessingBundle currencyFutureProcessingBundle) {
        return currencyRateRepository.countByPublicationDate(currencyFutureProcessingBundle.getPublicationDate())
                .filter(count -> count == 0)
                .peek(count -> currencyRateRepository.archiveCurrencyRates())
                .map(count -> currencyRateCreator.from(currencyFutureProcessingBundle))
                .map(currencyRateRepository::saveAll);
    }

    //Only for demo purpose
    public Set<TransactionEntity> saveInitialTransactions(UserEntity userEntity) {
        return HashSet.ofAll(currencyRepository.findAll())
                .map(currencyEntity ->
                        TransactionEntity.builder()
                                .userEntity(userEntity)
                                .currencyEntity(currencyEntity)
                                .amount(BigDecimal.ZERO)
                                .currencyRate(BigDecimal.ONE)
                                .createdAt(LocalDateTime.now())
                                .build())
                .toSet();
    }

    //Only for demo purpose
    public Option<TransactionEntity> addFundsForUserForDemonstration(UserEntity userEntity) {
        return currencyRepository.findByCode("PLN")
                .map(currencyEntity -> TransactionEntity.builder()
                        .userEntity(userEntity)
                        .currencyEntity(currencyEntity)
                        .amount(BigDecimal.valueOf(10000))
                        .currencyRate(BigDecimal.ONE)
                        .createdAt(LocalDateTime.now())
                        .build());
    }
}
