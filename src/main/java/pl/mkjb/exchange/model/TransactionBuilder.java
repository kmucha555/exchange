package pl.mkjb.exchange.model;

import lombok.Builder;
import lombok.Getter;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.util.TransactionTypeConstant;

import java.math.BigDecimal;

@Builder
@Getter
public class TransactionBuilder {
    private final CurrencyRateEntity currencyRateEntity;
    private final BigDecimal transactionAmount;
    private final BigDecimal transactionPrice;
    private final UserEntity userEntity;
    private final TransactionTypeConstant transactionTypeConstant;
}
