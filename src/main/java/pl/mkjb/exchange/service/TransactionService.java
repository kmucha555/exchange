package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.CurrencyModel;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService implements Transaction {
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final UserService userService;
    private final ExchangeService exchangeService;
    private final TransactionRepository transactionRepository;

    @Override
    public boolean hasErrors(TransactionModel transactionModel, long userId) {
        if (!currencyService.isValidCurrencyRate(transactionModel.getCurrencyRateId())) {
            return true;
        }
        val currencyModel = currencyService.findCurrencyByCurrencyRate(transactionModel.getCurrencyRateId());
        val buyAmount = transactionModel.getTransactionAmount();

        return buyAmount.compareTo(BigDecimal.ZERO) <= 0 ||
                buyAmount.remainder(BigDecimal.valueOf(currencyModel.getUnit())).compareTo(BigDecimal.ZERO) != 0 ||
                buyAmount.compareTo(estimateMaxTransactionAmount(currencyModel, userId)) > 0;
    }

    @Override
    public TransactionModel getTransactionModel(UUID currencyRateId, long userId) {
        val currencyModel = currencyService.findCurrencyByCurrencyRate(currencyRateId);

        return TransactionModel.builder()
                .currencyRateId(currencyModel.getCurrencyRateId())
                .currencyCode(currencyModel.getCode())
                .currencyUnit(currencyModel.getUnit())
                .transactionPrice(currencyModel.getSellPrice())
                .userWalletAmount(walletService.getUserWalletBaseCurrencyAmount(userId))
                .maxAllowedTransactionAmount(estimateMaxTransactionAmount(currencyModel, userId))
                .build();
    }

    private BigDecimal estimateMaxTransactionAmount(CurrencyModel currencyModel, long userId) {
        val userWalletAmount = walletService.getUserWalletBaseCurrencyAmount(userId);
        val exchangeCurrencyAmount = calculateAvailableCurrency(currencyModel.getCurrencyId());

        return userWalletAmount.divide(currencyModel.getSellPrice(), 0, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(currencyModel.getUnit()))
                .min(exchangeCurrencyAmount);
    }

    public BigDecimal calculateAvailableCurrency(int currencyId, long userId) {
        val userEntity = userService.findById(userId);
        return transactionRepository.sumCurrencyAmountForUser(userEntity.getId(), currencyId)
                .getOrElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateAvailableCurrency(int currencyId) {
        final UserEntity ownerEntity = userService.findOwner();
        return calculateAvailableCurrency(currencyId, ownerEntity.getId());
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionModel transactionModel, long userId) {
        val currencyModel = currencyService.findCurrencyByCurrencyRate(transactionModel.getCurrencyRateId());
        val transactionAmount = transactionModel.getTransactionAmount();
        exchangeService.sellCurrency(currencyModel, transactionAmount, userId);
    }
}
