package pl.mkjb.exchange.infrastructure.mvc.validator;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.passay.PasswordData;
import org.passay.PasswordValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class CustomPasswordValidator implements ConstraintValidator<Password, String> {
    private final PasswordValidator passwordValidator;

    @Override
    public boolean isValid(String passwordCandidate, ConstraintValidatorContext constraintValidatorContext) {
        val passwordCandidateData = new PasswordData(passwordCandidate);
        return passwordValidator.validate(passwordCandidateData).isValid();
    }
}
