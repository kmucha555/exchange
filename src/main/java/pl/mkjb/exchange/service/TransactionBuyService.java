package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.TransactionBuilder;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static pl.mkjb.exchange.util.TransactionType.BUY;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionBuyService implements Transaction {
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
                buyAmount.remainder(currencyRateEntity.getCurrencyEntity().getUnit()).compareTo(BigDecimal.ZERO) != 0 ||
                buyAmount.compareTo(estimateMaxTransactionAmount(currencyRateEntity, userId)) > 0;
    }

    @Override
    public TransactionModel getTransactionModel(UUID currencyRateId, long userId) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(currencyRateId);

        return TransactionModel.builder()
                .currencyRateId(currencyRateEntity.getId())
                .currencyCode(currencyRateEntity.getCurrencyEntity().getCode())
                .currencyUnit(currencyRateEntity.getCurrencyEntity().getUnit())
                .transactionPrice(currencyRateEntity.getSellPrice())
                .userWalletAmount(walletService.getUserWalletAmountForBillingCurrency(userId))
                .maxAllowedTransactionAmount(estimateMaxTransactionAmount(currencyRateEntity, userId))
                .build();
    }

    private BigDecimal estimateMaxTransactionAmount(CurrencyRateEntity currencyRateEntity, long userId) {
        val userWalletAmount = walletService.getUserWalletAmountForBillingCurrency(userId);
        val exchangeCurrencyAmount = calculateAvailableCurrency(currencyRateEntity.getCurrencyEntity().getId());

        return userWalletAmount.divide(currencyRateEntity.getSellPrice(), 0, RoundingMode.DOWN)
                .multiply(currencyRateEntity.getCurrencyEntity().getUnit())
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
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId());
        val transactionBuilder = TransactionBuilder.builder()
                .currencyRateEntity(currencyRateEntity)
                .transactionAmount(transactionModel.getTransactionAmount())
                .transactionPrice(currencyRateEntity.getSellPrice())
                .userId(userId)
                .transactionType(BUY)
                .build();
        val transactionEntities = exchangeService.prepareTransactionToSave(transactionBuilder);
        exchangeService.saveTransaction(transactionEntities);
    }
}
