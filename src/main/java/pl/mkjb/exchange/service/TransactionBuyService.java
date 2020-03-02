package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.math.RoundingMode;
import java.util.UUID;

import static pl.mkjb.exchange.util.TransactionTypeConstant.BUY;

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
    public TransactionModel getTransactionModel(UUID currencyRateId, UserDetails userDetails) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(currencyRateId);
        val userWalletAmountForBillingCurrency = walletService.getUserWalletAmountForBillingCurrency(userDetails);
        val maxAllowedTransactionAmount = estimateMaxTransactionAmount(currencyRateEntity, userDetails);

        return TransactionModel.builder()
                .currencyRateId(currencyRateEntity.getId())
                .currencyCode(currencyRateEntity.getCurrencyEntity().getCode())
                .currencyUnit(currencyRateEntity.getCurrencyEntity().getUnit())
                .transactionPrice(currencyRateEntity.getSellPrice())
                .userWalletAmount(userWalletAmountForBillingCurrency)
                .maxAllowedTransactionAmount(maxAllowedTransactionAmount)
                .transactionTypeConstant(BUY)
                .build();
    }

    public BigDecimal estimateMaxTransactionAmount(CurrencyRateEntity currencyRateEntity, UserDetails userDetails) {
        val userWalletAmount = walletService.getUserWalletAmountForBillingCurrency(userDetails);

        val exchangeCurrencyAmount = calculateAvailableCurrency(currencyRateEntity.getCurrencyEntity().getId());

        return userWalletAmount.divide(currencyRateEntity.getSellPrice(), 0, RoundingMode.DOWN)
                .multiply(currencyRateEntity.getCurrencyEntity().getUnit())
                .min(exchangeCurrencyAmount);
    }

    public BigDecimal calculateAvailableCurrency(int currencyId, UserEntity userEntity) {
        return transactionRepository.sumCurrencyAmountForUser(userEntity.getId(), currencyId)
                .getOrElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateAvailableCurrency(int currencyId) {
        final UserEntity ownerEntity = userService.findOwner();
        return calculateAvailableCurrency(currencyId, ownerEntity);
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionModel transactionModel, UserDetails userDetails) {
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId());
        val transactionBuilder = TransactionBuilder.builder()
                .currencyRateEntity(currencyRateEntity)
                .transactionAmount(transactionModel.getTransactionAmount())
                .transactionPrice(currencyRateEntity.getSellPrice())
                .userDetails(userDetails)
                .transactionTypeConstant(BUY)
                .build();
        val transactionEntities = exchangeService.prepareTransactionToSave(transactionBuilder);
        exchangeService.saveTransaction(transactionEntities);
    }
}
