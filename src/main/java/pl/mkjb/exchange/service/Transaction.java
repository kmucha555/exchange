package pl.mkjb.exchange.service;

import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.model.TransactionModel;

import java.util.UUID;

public interface Transaction {
    boolean hasErrors(TransactionModel transactionModel, UserDetails userDetails);

    TransactionModel getTransactionModel(UUID currencyRateId, UserDetails userDetails);

    void saveTransaction(TransactionModel transactionModel, UserDetails userDetails);
}
