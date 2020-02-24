package pl.mkjb.exchange.validator;

import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomPasswordValidator implements ConstraintValidator<Password, String> {
    private final PasswordValidator passwordValidator;

    public CustomPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Override
    public boolean isValid(String passwordCandidate, ConstraintValidatorContext constraintValidatorContext) {

        final RuleResult result = passwordValidator.validate(new PasswordData(passwordCandidate));
        return result.isValid();
    }
}
