package pl.mkjb.exchange.infrastructure.mvc.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TransactionTypeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionType {
    String message() default "{transaction.type.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
