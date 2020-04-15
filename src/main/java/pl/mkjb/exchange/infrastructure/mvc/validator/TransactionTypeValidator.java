package pl.mkjb.exchange.infrastructure.mvc.validator;

import pl.mkjb.exchange.infrastructure.util.TransactionTypeConstant;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class TransactionTypeValidator implements ConstraintValidator<TransactionType, TransactionTypeConstant> {
    @Override
    public boolean isValid(TransactionTypeConstant transactionTypeConstant, ConstraintValidatorContext context) {
        return transactionTypeConstant.equals(TransactionTypeConstant.BUY) ||
                transactionTypeConstant.equals(TransactionTypeConstant.SELL);
    }
}
