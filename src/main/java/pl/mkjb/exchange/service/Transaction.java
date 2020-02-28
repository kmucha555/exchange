package pl.mkjb.exchange.service;

import pl.mkjb.exchange.model.TransactionModel;

import java.util.UUID;

public interface Transaction {
    boolean hasErrors(TransactionModel transactionModel, long userId);
    TransactionModel getTransactionModel(UUID currencyRateId, long userId);
    void saveTransaction(TransactionModel transactionModel, long id);
}
