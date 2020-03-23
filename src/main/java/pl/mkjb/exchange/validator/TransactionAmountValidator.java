package pl.mkjb.exchange.validator;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.service.CurrencyService;
import pl.mkjb.exchange.service.TransactionFacadeService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.function.Predicate;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class TransactionAmountValidator implements ConstraintValidator<TransactionAmount, TransactionModel> {
    private String transactionAmountFieldName;
    private final CurrencyService currencyService;
    private final TransactionFacadeService transactionFacadeService;

    @Override
    public void initialize(TransactionAmount constraintAnnotation) {
        this.transactionAmountFieldName = constraintAnnotation.transactionAmountFieldName();
    }

    @Override
    public boolean isValid(TransactionModel transactionModel, ConstraintValidatorContext context) {
        val isValid = isTransactionAmountGreaterThenZero()
                .and(isTransactionAmountDivisibleByCurrencyUnit())
                .and(isTransactionAmountLessThenFundsAvailableInExchange())
                .test(transactionModel);

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(transactionAmountFieldName)
                    .addConstraintViolation();
        }

        return isValid;
    }

    public Predicate<TransactionModel> isTransactionAmountGreaterThenZero() {
        return transactionModel -> transactionModel.getTransactionAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public Predicate<TransactionModel> isTransactionAmountDivisibleByCurrencyUnit() {
        return transactionModel -> {
            val currencyUnit = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId())
                    .getCurrencyEntity()
                    .getUnit();
            val transactionAmount = transactionModel.getTransactionAmount();
            return transactionAmount.remainder(currencyUnit).compareTo(BigDecimal.ZERO) == 0;
        };
    }

    public Predicate<TransactionModel> isTransactionAmountLessThenFundsAvailableInExchange() {
        return transactionModel -> {
            val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId());
            val maxTransactionAmount = transactionFacadeService.estimateMaxTransactionAmount()
                    .apply(transactionModel.getTransactionTypeConstant())
                    .apply(currencyRateEntity, getCustomUser().get());
            return transactionModel.getTransactionAmount().compareTo(maxTransactionAmount) < 1;
        };
    }

    public Supplier<UserDetails> getCustomUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return () -> (UserDetails) authentication.getPrincipal();
    }
}
