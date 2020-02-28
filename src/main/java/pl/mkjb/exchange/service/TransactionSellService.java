package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.TransactionBuilder;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static java.math.RoundingMode.DOWN;
import static pl.mkjb.exchange.util.TransactionType.SELL;

@Service
@RequiredArgsConstructor
public class TransactionSellService implements Transaction {
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final UserService userService;
    private final ExchangeService exchangeService;
    private final TransactionRepository transactionRepository;

    @Override
    public boolean hasErrors(TransactionModel transactionModel, long userId) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId());
        val buyAmount = transactionModel.getTransactionAmount();

        return buyAmount.compareTo(BigDecimal.ZERO) <= 0 ||
                buyAmount.remainder(BigDecimal.valueOf(currencyRateEntity.getCurrencyEntity().getUnit())).compareTo(BigDecimal.ZERO) != 0 ||
                buyAmount.compareTo(estimateMaxTransactionAmount(currencyRateEntity, userId)) > 0;
    }

    @Override
    public TransactionModel getTransactionModel(UUID currencyRateId, long userId) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(currencyRateId);

        return TransactionModel.builder()
                .currencyRateId(currencyRateEntity.getId())
                .currencyCode(currencyRateEntity.getCurrencyEntity().getCode())
                .currencyUnit(currencyRateEntity.getCurrencyEntity().getUnit())
                .transactionPrice(currencyRateEntity.getPurchasePrice())
                .userWalletAmount(walletService.getUserWalletAmountForGivenCurrency(currencyRateId, userId).setScale(0, DOWN))
                .maxAllowedTransactionAmount(estimateMaxTransactionAmount(currencyRateEntity, userId).setScale(0, DOWN))
                .build();
    }

    private BigDecimal estimateMaxTransactionAmount(CurrencyRateEntity currencyRateEntity, long userId) {
        val baseCurrencyRateEntity = currencyService.findBaseCurrencyRate();
        val userWalletAmount = walletService.getUserWalletAmountForGivenCurrency(currencyRateEntity.getId(), userId);
        val exchangeCurrencyAmount = calculateAvailableCurrency(baseCurrencyRateEntity)
                .divide(currencyRateEntity.getPurchasePrice(), 0, DOWN)
                .multiply(BigDecimal.valueOf(currencyRateEntity.getCurrencyEntity().getUnit()));
        return userWalletAmount.min(exchangeCurrencyAmount);
    }

    private BigDecimal calculateAvailableCurrency(CurrencyRateEntity currencyRateEntity, long userId) {
        val userEntity = userService.findById(userId);
        return transactionRepository
                .sumCurrencyAmountForUser(userEntity.getId(), currencyRateEntity.getCurrencyEntity().getId())
                .getOrElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateAvailableCurrency(CurrencyRateEntity currencyRateEntity) {
        final UserEntity ownerEntity = userService.findOwner();
        return calculateAvailableCurrency(currencyRateEntity, ownerEntity.getId());
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionModel transactionModel, long userId) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId());
        val transactionBuilder = TransactionBuilder.builder()
                .currencyRateEntity(currencyRateEntity)
                .transactionAmount(transactionModel.getTransactionAmount())
                .transactionPrice(currencyRateEntity.getPurchasePrice())
                .userId(userId)
                .transactionType(SELL)
                .build();
        val transactionEntities = exchangeService.prepareTransactionToSave(transactionBuilder);
        exchangeService.saveTransaction(transactionEntities);
    }
}
