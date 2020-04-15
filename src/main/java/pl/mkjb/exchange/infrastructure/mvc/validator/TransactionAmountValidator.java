package pl.mkjb.exchange.infrastructure.mvc.validator;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.currency.domain.CurrencyFacade;
import pl.mkjb.exchange.currency.dto.CurrencyDto;
import pl.mkjb.exchange.currency.dto.CurrencyRateDto;
import pl.mkjb.exchange.transaction.domain.TransactionFacade;
import pl.mkjb.exchange.transaction.dto.TransactionDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.function.Predicate;
import java.util.function.Supplier;

@RequiredArgsConstructor
class TransactionAmountValidator implements ConstraintValidator<TransactionAmount, TransactionDto> {
    private String transactionAmountFieldName;
    private final CurrencyFacade currencyFacade;
    private final TransactionFacade transactionFacade;

    @Override
    public void initialize(TransactionAmount constraintAnnotation) {
        this.transactionAmountFieldName = constraintAnnotation.transactionAmountFieldName();
    }

    @Override
    public boolean isValid(TransactionDto transactionDto, ConstraintValidatorContext context) {
        val isValid = isTransactionAmountGreaterThenZero()
                .and(isTransactionAmountDivisibleByCurrencyUnit())
                .and(isTransactionAmountLessThenFundsAvailableInExchange())
                .test(transactionDto);

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(transactionAmountFieldName)
                    .addConstraintViolation();
        }

        return isValid;
    }

    private Predicate<TransactionDto> isTransactionAmountGreaterThenZero() {
        return transactionModel -> transactionModel.getTransactionAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    private Predicate<TransactionDto> isTransactionAmountDivisibleByCurrencyUnit() {
        return transaction -> currencyFacade.findCurrencyRateByCurrencyRateId(transaction.getCurrencyRateId())
                .map(CurrencyRateDto::getCurrencyDto)
                .map(CurrencyDto::getUnit)
                .map(unit -> transaction.getTransactionAmount().remainder(unit))
                .map(remainder -> remainder.compareTo(BigDecimal.ZERO) == 0)
                .getOrElse(false);
    }

    private Predicate<TransactionDto> isTransactionAmountLessThenFundsAvailableInExchange() {
        return transaction -> currencyFacade.findCurrencyRateByCurrencyRateId(transaction.getCurrencyRateId())
                .map(currencyRate -> transactionFacade.estimateMaxTransactionAmount()
                        .apply(transaction.getTransactionTypeConstant())
                        .apply(currencyRate, getCustomUser().get()))
                .map(maxTransactionAmount -> transaction.getTransactionAmount().compareTo(maxTransactionAmount) < 1)
                .getOrElse(false);
    }

    private Supplier<UserDetails> getCustomUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return () -> (UserDetails) authentication.getPrincipal();
    }
}
