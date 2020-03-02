package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
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
import static pl.mkjb.exchange.util.TransactionTypeConstant.SELL;

@Service
@RequiredArgsConstructor
public class TransactionSellService implements Transaction {
    private final WalletService walletService;
    private final CurrencyService currencyService;
    private final UserService userService;
    private final ExchangeService exchangeService;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionModel getTransactionModel(UUID currencyRateId, UserDetails userDetails) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(currencyRateId);

        return TransactionModel.builder()
                .currencyRateId(currencyRateEntity.getId())
                .currencyCode(currencyRateEntity.getCurrencyEntity().getCode())
                .currencyUnit(currencyRateEntity.getCurrencyEntity().getUnit())
                .transactionPrice(currencyRateEntity.getPurchasePrice())
                .userWalletAmount(walletService.getUserWalletAmountForGivenCurrency(currencyRateId, userDetails).setScale(0, DOWN))
                .maxAllowedTransactionAmount(estimateMaxTransactionAmount(currencyRateEntity, userDetails).setScale(0, DOWN))
                .transactionTypeConstant(SELL)
                .build();
    }

    public BigDecimal estimateMaxTransactionAmount(CurrencyRateEntity currencyRateEntity, UserDetails userDetails) {
        val billingCurrencyRateEntity = currencyService.findBillingCurrencyRate();
        val userWalletAmount = walletService.getUserWalletAmountForGivenCurrency(currencyRateEntity.getId(), userDetails);
        val exchangeCurrencyAmount = calculateAvailableCurrency(billingCurrencyRateEntity)
                .divide(currencyRateEntity.getPurchasePrice(), 0, DOWN)
                .multiply(currencyRateEntity.getCurrencyEntity().getUnit());
        return userWalletAmount.min(exchangeCurrencyAmount);
    }

    private BigDecimal calculateAvailableCurrency(CurrencyRateEntity currencyRateEntity) {
        final UserEntity ownerEntity = userService.findOwner();
        return transactionRepository
                .sumCurrencyAmountForUser(ownerEntity.getId(), currencyRateEntity.getCurrencyEntity().getId())
                .getOrElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionModel transactionModel, UserDetails userDetails) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId());
        val transactionBuilder = TransactionBuilder.builder()
                .currencyRateEntity(currencyRateEntity)
                .transactionAmount(transactionModel.getTransactionAmount())
                .transactionPrice(currencyRateEntity.getPurchasePrice())
                .userDetails(userDetails)
                .transactionTypeConstant(SELL)
                .build();
        val transactionEntities = exchangeService.prepareTransactionToSave(transactionBuilder);
        exchangeService.saveTransaction(transactionEntities);
    }
}
