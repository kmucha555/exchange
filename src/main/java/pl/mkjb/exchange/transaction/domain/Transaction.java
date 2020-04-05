package pl.mkjb.exchange.transaction.domain;

import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.transaction.dto.TransactionDto;

import java.math.BigDecimal;
import java.util.UUID;

interface Transaction {
    TransactionDto from(UUID currencyRateId, UserDetails userDetails);

    BigDecimal estimateMaxAllowedTransactionAmountForUser(CurrencyRateDto currencyRateDto, UserDetails userDetails);

    void saveTransaction(TransactionDto transactionDto, UserDetails userDetails);
}
