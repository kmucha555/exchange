package pl.mkjb.exchange.infrastructure.mvc.validator;

import lombok.val;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class EqualPasswordsValidator implements ConstraintValidator<EqualFields, Object> {
    private String field;
    private String fieldConfirm;
    private String fieldMatchName;

    @Override
    public void initialize(EqualFields constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldConfirm = constraintAnnotation.fieldConfirm();
        this.fieldMatchName = constraintAnnotation.fieldMatchName();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        Object fieldValue = new BeanWrapperImpl(object)
                .getPropertyValue(field);
        Object fieldValueConfirm = new BeanWrapperImpl(object)
                .getPropertyValue(fieldConfirm);

        val areEqual = fieldValue != null && fieldValue.equals(fieldValueConfirm);

        if (!areEqual) {
            constraintValidatorContext.disableDefaultConstraintViolation();

            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    constraintValidatorContext.
                            getDefaultConstraintMessageTemplate())
                    .addPropertyNode(fieldMatchName)
                    .addConstraintViolation();
        }

        return areEqual;
    }
}
