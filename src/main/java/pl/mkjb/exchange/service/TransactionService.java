package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.TransactionBuyModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final ExchangeService exchangeService;

    public TransactionBuyModel getTransactionBuyModel(UUID currencyId, long userId) {
        final CurrencyModel currencyModel = currencyService.findCurrency(currencyId);

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
        final BigDecimal userWalletAmount = walletService.getUserWalletBaseCurrencyAmount(userId);
        final BigDecimal exchangeCurrencyAmount = exchangeService.calculateAvailableCurrency(currencyModel.getCurrencyId());
        final BigDecimal possibleAmountToBuyByUser =
                userWalletAmount.divide(currencyModel.getSellPrice(), 0, RoundingMode.DOWN)
                        .multiply(new BigDecimal(currencyModel.getUnit()));
        return possibleAmountToBuyByUser.min(exchangeCurrencyAmount);

    }

}
