package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.TransactionBuyModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final ExchangeService exchangeService;

    public TransactionBuyModel getTransactionBuyModel(UUID currencyRateId, long userId) {
        val currencyModel = currencyService.findCurrency(currencyRateId);

        return TransactionBuyModel.builder()
                .currencyRateId(currencyModel.getCurrencyRateId())
                .currencyCode(currencyModel.getCode())
                .currencyUnit(currencyModel.getUnit())
                .sellPrice(currencyModel.getSellPrice())
                .userWalletAmount(walletService.getUserWalletBaseCurrencyAmount(userId))
                .maxAmountOfCurrencyForBuyByUser(estimateMaxAmountOfCurrencyToBuyByUser(currencyModel, userId))
                .build();
    }

    private BigDecimal estimateMaxAmountOfCurrencyToBuyByUser(CurrencyModel currencyModel, long userId) {
        val userWalletAmount = walletService.getUserWalletBaseCurrencyAmount(userId);
        val exchangeCurrencyAmount = exchangeService.calculateAvailableCurrency(currencyModel.getCurrencyId());

        return userWalletAmount.divide(currencyModel.getSellPrice(), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(currencyModel.getUnit()))
                .min(exchangeCurrencyAmount);
    }

    public boolean hasErrors(TransactionBuyModel transactionBuyModel, long userId) {
        if (!currencyService.isValidCurrencyRateId(transactionBuyModel.getCurrencyRateId())) {
            return true;
        }
        val currencyModel = currencyService.findCurrency(transactionBuyModel.getCurrencyRateId());
        val buyAmount = transactionBuyModel.getBuyAmount();

        return buyAmount.compareTo(BigDecimal.ZERO) <= 0 ||
                buyAmount.remainder(BigDecimal.valueOf(currencyModel.getUnit())).compareTo(BigDecimal.ZERO) != 0 ||
                buyAmount.compareTo(estimateMaxAmountOfCurrencyToBuyByUser(currencyModel, userId)) > 0;
    }
}
