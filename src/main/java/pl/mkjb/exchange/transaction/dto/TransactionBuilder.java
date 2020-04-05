package pl.mkjb.exchange.transaction.dto;

import lombok.Builder;
import lombok.Getter;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant;
import pl.mkjb.exchange.user.domain.UserEntity;

import java.math.BigDecimal;

@Builder
@Getter
public class TransactionBuilder {
    private final CurrencyRateDto currencyRateDto;
    private final BigDecimal transactionAmount;
    private final BigDecimal transactionPrice;
    private final UserEntity userEntity;
    private final TransactionTypeConstant transactionTypeConstant;
}
