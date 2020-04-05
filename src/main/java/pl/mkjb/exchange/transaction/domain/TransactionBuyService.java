package pl.mkjb.exchange.transaction.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.infrastructure.CurrencyNotFoundException;
import pl.mkjb.exchange.transaction.dto.TransactionBuilder;
import pl.mkjb.exchange.transaction.dto.TransactionDto;
import pl.mkjb.exchange.user.domain.UserService;
import pl.mkjb.exchange.wallet.domain.WalletFacade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant.BUY;
import static pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant.SELL;

@Slf4j
@RequiredArgsConstructor
class TransactionBuyService implements Transaction {
    private final WalletFacade walletFacade;
    private final CurrencyFacade currencyFacade;
    private final UserService userService;
    private final ExchangeService exchangeService;
    private final TransactionRepository transactionRepository;

    @Override
    public TransactionDto from(UUID currencyRateId, UserDetails userDetails) {
        return currencyFacade.findCurrencyRateByCurrencyRateId(currencyRateId)
                .map(currencyRate -> TransactionDto.builder()
                        .currencyRateId(currencyRate.getId())
                        .currencyCode(currencyRate.getCurrencyDto().getCode())
                        .currencyUnit(currencyRate.getCurrencyDto().getUnit())
                        .transactionPrice(currencyRate.getSellPrice())
                        .userWalletAmount(walletFacade.getUserWalletAmountForBillingCurrency(userDetails))
                        .maxAllowedTransactionAmount(estimateMaxAllowedTransactionAmountForUser(currencyRate, userDetails))
                        .transactionTypeConstant(BUY)
                        .build())
                .peek(transactionDto -> log.info("Transaction DTO {}", transactionDto))
                .getOrElseThrow(() -> new CurrencyNotFoundException(""));
    }

    public BigDecimal estimateMaxAllowedTransactionAmountForUser(CurrencyRateDto currencyRateDto, UserDetails userDetails) {
        val userWalletAmount = getMaxTransactionAmount(currencyRateDto, userDetails);

        return currencyFacade.findBillingCurrency()
                .map(this::calculateAvailableCurrency)
                .map(userWalletAmount::min)
                .getOrElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateAvailableCurrency(CurrencyRateDto currencyRateDto) {
        val userEntity = userService.findOwner();

        return transactionRepository.sumCurrencyAmountForUser(userEntity.getId(), currencyRateDto.getCurrencyDto().getId())
                .getOrElse(BigDecimal.ZERO);
    }

    private BigDecimal getMaxTransactionAmount(CurrencyRateDto currencyRateDto, UserDetails userDetails) {
        return walletFacade.getUserWalletAmountForBillingCurrency(userDetails)
                .divide(currencyRateDto.getSellPrice(), 0, RoundingMode.DOWN)
                .multiply(currencyRateDto.getCurrencyDto().getUnit());
    }

    @Override
    @Transactional
    public void saveTransaction(TransactionDto transactionDto, UserDetails userDetails) {
        val userEntity = userService.findByUsername(userDetails.getUsername());

        currencyFacade.findCurrencyRateByCurrencyRateId(transactionDto.getCurrencyRateId())
                .map(currency -> TransactionBuilder.builder()
                        .currencyRateEntity(currencyFacade.from(currency))
                        .transactionAmount(transactionDto.getTransactionAmount().negate())
                        .transactionPrice(currency.getSellPrice())
                        .userEntity(userEntity)
                        .transactionTypeConstant(SELL)
                        .build())
                .map(exchangeService::prepareTransactionToSave)
                .forEach(exchangeService::saveCompleteTransaction);
    }
}
