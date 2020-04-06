package pl.mkjb.exchange.infrastructure.mvc.validator;

import lombok.RequiredArgsConstructor;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

@RequiredArgsConstructor
class CustomPasswordValidator implements ConstraintValidator<Password, String> {
    private final PasswordValidator passwordValidator;

    @Override
    public boolean isValid(String passwordCandidate, ConstraintValidatorContext constraintValidatorContext) {
        return Optional.ofNullable(passwordCandidate)
                .map(PasswordData::new)
                .map(passwordValidator::validate)
                .map(RuleResult::isValid)
                .orElse(false);
    }
}
