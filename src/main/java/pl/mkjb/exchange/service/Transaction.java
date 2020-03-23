package pl.mkjb.exchange.service;

import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.TransactionModel;

import java.math.BigDecimal;
import java.util.UUID;

public interface Transaction {
    TransactionModel getTransactionModel(UUID currencyRateId, UserDetails userDetails);

    BigDecimal estimateMaxAllowedTransactionAmountForUser(CurrencyRateEntity currencyRateEntity, UserDetails userDetails);

    void saveTransaction(TransactionModel transactionModel, UserDetails userDetails);
}
