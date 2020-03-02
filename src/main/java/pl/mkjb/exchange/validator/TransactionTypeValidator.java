package pl.mkjb.exchange.validator;

import pl.mkjb.exchange.util.TransactionTypeConstant;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TransactionTypeValidator implements ConstraintValidator<TransactionType, TransactionTypeConstant> {
    @Override
    public boolean isValid(TransactionTypeConstant transactionTypeConstant, ConstraintValidatorContext context) {
        return transactionTypeConstant.equals(TransactionTypeConstant.BUY) ||
                transactionTypeConstant.equals(TransactionTypeConstant.SELL);
    }
}
