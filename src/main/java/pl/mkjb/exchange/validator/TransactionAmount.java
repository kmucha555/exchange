package pl.mkjb.exchange.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {TransactionAmountValidator.class})
public @interface TransactionAmount {
    String transactionAmountFieldName();

    String message() default "{transaction.amount.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
