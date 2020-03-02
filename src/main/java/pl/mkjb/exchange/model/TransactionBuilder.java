package pl.mkjb.exchange.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.util.TransactionTypeConstant;

import java.math.BigDecimal;

@Builder
@Getter
public class TransactionBuilder {
    private final CurrencyRateEntity currencyRateEntity;
    private final BigDecimal transactionAmount;
    private final BigDecimal transactionPrice;
    private final UserDetails userDetails;
    private final TransactionTypeConstant transactionTypeConstant;
}
