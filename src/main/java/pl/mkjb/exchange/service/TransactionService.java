package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.TransactionModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService implements Transaction {
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final ExchangeService exchangeService;

    @Override
    public boolean hasErrors(TransactionModel transactionModel, long userId) {
        if (!currencyService.isValidCurrencyRateId(transactionModel.getCurrencyRateId())) {
            return true;
        }
        val currencyModel = currencyService.findCurrency(transactionModel.getCurrencyRateId());
        val buyAmount = transactionModel.getTransactionAmount();

        return buyAmount.compareTo(BigDecimal.ZERO) <= 0 ||
                buyAmount.remainder(BigDecimal.valueOf(currencyModel.getUnit())).compareTo(BigDecimal.ZERO) != 0 ||
                buyAmount.compareTo(estimateMaxTransactionAmount(currencyModel, userId)) > 0;
    }

    @Override
    public TransactionModel getTransactionModel(UUID currencyRateId, long userId) {
        val currencyModel = currencyService.findCurrency(currencyRateId);

        return TransactionModel.builder()
                .currencyRateId(currencyModel.getCurrencyRateId())
                .currencyCode(currencyModel.getCode())
                .currencyUnit(currencyModel.getUnit())
                .transactionPrice(currencyModel.getSellPrice())
                .userWalletAmount(walletService.getUserWalletBaseCurrencyAmount(userId))
                .maxAllowedTransactionAmount(estimateMaxTransactionAmount(currencyModel, userId))
                .build();
    }

    @Override
    public BigDecimal estimateMaxTransactionAmount(CurrencyModel currencyModel, long userId) {
        val userWalletAmount = walletService.getUserWalletBaseCurrencyAmount(userId);
        val exchangeCurrencyAmount = exchangeService.calculateAvailableCurrency(currencyModel.getCurrencyId());

        return userWalletAmount.divide(currencyModel.getSellPrice(), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(currencyModel.getUnit()))
                .min(exchangeCurrencyAmount);
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionModel transactionModel) {

    }
}
