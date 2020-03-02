package pl.mkjb.exchange.service;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.util.TransactionTypeConstant;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class TransactionFacadeService {
    private final Transaction transactionBuyService;
    private final Transaction transactionSellService;

    public Function1<TransactionTypeConstant, Function2<UUID, UserDetails, TransactionModel>> getTransactionModel() {
        return transactionType -> Option.of(transactionType)
                .filter(transaction -> transaction.name().equals(TransactionTypeConstant.BUY.name()))
                .map(transaction -> getBuyTransactionModel())
                .getOrElse(this::getSellTransactionModel);
    }

    public Function1<TransactionTypeConstant, BiConsumer<TransactionModel, UserDetails>> saveTransaction() {
        return transactionType -> Option.of(transactionType)
                .filter(transaction -> transaction.name().equals(TransactionTypeConstant.BUY.name()))
                .map(transaction -> saveBuyTransaction())
                .getOrElse(this::saveSellTransaction);
    }

    public Function1<TransactionTypeConstant, Function2<CurrencyRateEntity, UserDetails, BigDecimal>> estimateMaxTransactionAmount() {
        return transactionType -> Option.of(transactionType)
                .filter(transaction -> transaction.name().equals(TransactionTypeConstant.BUY.name()))
                .map(transaction -> estimateMaxBuyTransactionAmount())
                .getOrElse(this::estimateMaxSellTransactionAmount);
    }

    private Function2<UUID, UserDetails, TransactionModel> getBuyTransactionModel() {
        return transactionBuyService::getTransactionModel;
    }

    private Function2<UUID, UserDetails, TransactionModel> getSellTransactionModel() {
        return transactionSellService::getTransactionModel;
    }

    private BiConsumer<TransactionModel, UserDetails> saveBuyTransaction() {
        return transactionBuyService::saveTransaction;
    }

    private BiConsumer<TransactionModel, UserDetails> saveSellTransaction() {
        return transactionSellService::saveTransaction;
    }

    private Function2<CurrencyRateEntity, UserDetails, BigDecimal> estimateMaxBuyTransactionAmount() {
        return transactionBuyService::estimateMaxTransactionAmount;
    }

    private Function2<CurrencyRateEntity, UserDetails, BigDecimal> estimateMaxSellTransactionAmount() {
        return transactionSellService::estimateMaxTransactionAmount;
    }
}
