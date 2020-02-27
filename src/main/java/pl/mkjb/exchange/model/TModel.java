package pl.mkjb.exchange.model;

import lombok.Builder;
import lombok.Getter;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.util.TransactionType;

import java.math.BigDecimal;

@Builder
@Getter
public class TModel {
    private final CurrencyRateEntity currencyRateEntity;
    private final BigDecimal transactionAmount;
    private final BigDecimal transactionPrice;
    private final long userId;
    private final TransactionType transactionType;
}
