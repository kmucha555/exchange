package pl.mkjb.exchange.transaction.domain;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant;
import pl.mkjb.exchange.transaction.dto.TransactionDto;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
class TransactionFacadeService {
    private final Transaction transactionBuyService;
    private final Transaction transactionSellService;

    public Function1<TransactionTypeConstant, Function2<UUID, UserDetails, TransactionDto>> getTransactionDto() {
        return transactionType -> Option.of(transactionType)
                .filter(transaction -> transaction.name().equals(TransactionTypeConstant.BUY.name()))
                .map(transaction -> getBuyTransactionModel())
                .getOrElse(this::getSellTransactionModel);
    }

    public Function1<TransactionTypeConstant, BiConsumer<TransactionDto, UserDetails>> saveTransaction() {
        return transactionType -> Option.of(transactionType)
                .filter(transaction -> transaction.name().equals(TransactionTypeConstant.BUY.name()))
                .map(transaction -> saveBuyTransaction())
                .getOrElse(this::saveSellTransaction);
    }

    public Function1<TransactionTypeConstant, Function2<CurrencyRateDto, UserDetails, BigDecimal>> estimateMaxTransactionAmount() {
        return transactionType -> Option.of(transactionType)
                .filter(transaction -> transaction.name().equals(TransactionTypeConstant.BUY.name()))
                .map(transaction -> estimateMaxBuyTransactionAmount())
                .getOrElse(this::estimateMaxSellTransactionAmount);
    }

    private Function2<UUID, UserDetails, TransactionDto> getBuyTransactionModel() {
        return transactionBuyService::from;
    }

    private Function2<UUID, UserDetails, TransactionDto> getSellTransactionModel() {
        return transactionSellService::from;
    }

    private BiConsumer<TransactionDto, UserDetails> saveBuyTransaction() {
        return transactionBuyService::saveTransaction;
    }

    private BiConsumer<TransactionDto, UserDetails> saveSellTransaction() {
        return transactionSellService::saveTransaction;
    }

    private Function2<CurrencyRateDto, UserDetails, BigDecimal> estimateMaxBuyTransactionAmount() {
        return transactionBuyService::estimateMaxAllowedTransactionAmountForUser;
    }

    private Function2<CurrencyRateDto, UserDetails, BigDecimal> estimateMaxSellTransactionAmount() {
        return transactionSellService::estimateMaxAllowedTransactionAmountForUser;
    }
}
