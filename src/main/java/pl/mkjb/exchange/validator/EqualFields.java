package pl.mkjb.exchange.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EqualPasswordsValidator.class})
public @interface EqualFields {
    String field();

    String fieldConfirm();

    String fieldMatchName();

    String message() default "{passwords.not.equal.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
