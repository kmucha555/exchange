package pl.mkjb.exchange.validator;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.mkjb.exchange.model.TransactionModel;
import pl.mkjb.exchange.security.CustomUser;
import pl.mkjb.exchange.service.CurrencyService;
import pl.mkjb.exchange.service.TransactionFacadeService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        val customUser = (CustomUser) authentication.getPrincipal();

        val transactionType = transactionModel.getTransactionTypeConstant();

        val currencyRateEntity = currencyService.findCurrencyRateByCurrencyRateId(transactionModel.getCurrencyRateId());
        val currencyUnit = currencyRateEntity.getCurrencyEntity().getUnit();
        val buyAmount = transactionModel.getTransactionAmount();
        val maxTransactionAmount = transactionFacadeService.estimateMaxTransactionAmount()
                .apply(transactionType)
                .apply(currencyRateEntity, customUser);

        val isValid = buyAmount.compareTo(BigDecimal.ZERO) > 0 &&
                buyAmount.remainder(currencyUnit).compareTo(BigDecimal.ZERO) == 0 &&
                buyAmount.compareTo(maxTransactionAmount) < 1;

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(transactionAmountFieldName)
                    .addConstraintViolation();
        }

        return isValid;
    }
}
