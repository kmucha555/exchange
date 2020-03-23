package pl.mkjb.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.entity.CurrencyRateEntity;
import pl.mkjb.exchange.entity.TransactionEntity;
import pl.mkjb.exchange.entity.UserEntity;
import pl.mkjb.exchange.model.TransactionBuilder;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
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
        val userWalletAmountInBillingCurrency = walletService.getUserWalletAmountForBillingCurrency(userDetails);
        val maxAllowedTransactionAmount = estimateMaxAllowedTransactionAmountForUser(currencyRateEntity, userDetails);

        return TransactionModel.builder()
                .currencyRateId(currencyRateEntity.getId())
                .currencyCode(currencyRateEntity.getCurrencyEntity().getCode())
                .currencyUnit(currencyRateEntity.getCurrencyEntity().getUnit())
                .transactionPrice(currencyRateEntity.getSellPrice())
                .userWalletAmount(userWalletAmountInBillingCurrency)
                .maxAllowedTransactionAmount(maxAllowedTransactionAmount)
                .transactionTypeConstant(BUY)
                .build();
    }

    public BigDecimal estimateMaxAllowedTransactionAmountForUser(CurrencyRateEntity currencyRateEntity, UserDetails userDetails) {
        final BigDecimal userWalletAmount = walletService.getUserWalletAmountForBillingCurrency(userDetails);
        final BigDecimal exchangeMaxCurrencyAmount = calculateAvailableCurrencyAmount(currencyRateEntity.getCurrencyEntity().getId());
        final BigDecimal userWalletAmountInGivenCurrency = userWalletAmount.divide(currencyRateEntity.getSellPrice(), 0, RoundingMode.DOWN)
                .multiply(currencyRateEntity.getCurrencyEntity().getUnit());

        return userWalletAmountInGivenCurrency.min(exchangeMaxCurrencyAmount);
    }

    public BigDecimal calculateAvailableCurrencyAmount(int currencyId, UserEntity userEntity) {
        return transactionRepository.sumCurrencyAmountForUser(userEntity.getId(), currencyId)
                .getOrElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateAvailableCurrencyAmount(int currencyId) {
        final UserEntity ownerEntity = userService.findOwner();
        return calculateAvailableCurrencyAmount(currencyId, ownerEntity);
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionModel transactionModel, UserDetails userDetails) {
        val userEntity = userService.findByUsername(userDetails.getUsername());
        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId());
        val transactionBuilder = TransactionBuilder.builder()
                .currencyRateEntity(currencyRateEntity)
                .transactionAmount(transactionModel.getTransactionAmount().negate())
                .transactionPrice(currencyRateEntity.getSellPrice())
                .userEntity(userEntity)
                .transactionTypeConstant(BUY)
                .build();
        final Set<TransactionEntity> transactionEntities = exchangeService.prepareTransactionToSave(transactionBuilder);

        exchangeService.saveTransaction(transactionEntities);
    }
}
