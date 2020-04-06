package pl.mkjb.exchange.transaction.domain;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.CurrencyNotFoundException;
import pl.mkjb.exchange.transaction.dto.TransactionBuilder;
import pl.mkjb.exchange.transaction.dto.TransactionDto;
import pl.mkjb.exchange.user.domain.UserFacade;
import pl.mkjb.exchange.wallet.domain.WalletFacade;

import java.math.BigDecimal;
import java.util.UUID;

import static java.math.RoundingMode.DOWN;
import static pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant.SELL;

@RequiredArgsConstructor
class TransactionSellService implements Transaction {
    private final WalletFacade walletFacade;
    private final CurrencyFacade currencyFacade;
    private final UserFacade userFacade;
    private final ExchangeService exchangeService;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionDto from(UUID currencyRateId, UserDetails userDetails) {
        return currencyFacade.findCurrencyRateByCurrencyRateId(currencyRateId)
                .map(currencyRate -> TransactionDto.builder()
                        .currencyRateId(currencyRate.getId())
                        .currencyCode(currencyRate.getCurrencyDto().getCode())
                        .currencyUnit(currencyRate.getCurrencyDto().getUnit())
                        .transactionPrice(currencyRate.getPurchasePrice())
                        .userWalletAmount(walletFacade.getUserWalletAmountForGivenCurrency(currencyRateId, userDetails).setScale(0, DOWN))
                        .maxAllowedTransactionAmount(estimateMaxAllowedTransactionAmountForUser(currencyRate, userDetails).setScale(0, DOWN))
                        .transactionTypeConstant(SELL)
                        .build())
                .getOrElseThrow(() -> new CurrencyNotFoundException(""));
    }

    public BigDecimal estimateMaxAllowedTransactionAmountForUser(CurrencyRateDto currencyRateDto, UserDetails userDetails) {
        val userWalletAmount = getMinTransactionAmount(currencyRateDto, userDetails);

        return currencyFacade.findBillingCurrency()
                .map(this::calculateAvailableCurrency)
                .map(userWalletAmount::min)
                .getOrElse(BigDecimal.ZERO);
    }

    private BigDecimal getMinTransactionAmount(CurrencyRateDto currencyRateDto, UserDetails userDetails) {
        return walletFacade.getUserWalletAmountForGivenCurrency(currencyRateDto.getId(), userDetails);
    }

    private BigDecimal calculateAvailableCurrency(CurrencyRateDto currencyRateDto) {
        val userEntity = userFacade.findOwner();

        return transactionRepository.sumCurrencyAmountForUser(userEntity.getId(), currencyRateDto.getCurrencyDto().getId())
                .map(amount -> amount.divide(currencyRateDto.getPurchasePrice(), 0, DOWN))
                .map(amount -> amount.multiply(currencyRateDto.getCurrencyDto().getUnit()))
                .getOrElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionDto transactionDto, UserDetails userDetails) {
        val userEntity = userFacade.findByUsername(userDetails.getUsername());

        currencyFacade.findCurrencyRateByCurrencyRateId(transactionDto.getCurrencyRateId())
                .map(currency -> TransactionBuilder.builder()
                        .currencyRateDto(currency)
                        .transactionAmount(transactionDto.getTransactionAmount())
                        .transactionPrice(currency.getPurchasePrice())
                        .userEntity(userEntity)
                        .transactionTypeConstant(SELL)
                        .build())
                .map(exchangeService::prepareTransactionToSave)
                .forEach(exchangeService::saveCompleteTransaction);
    }
}
