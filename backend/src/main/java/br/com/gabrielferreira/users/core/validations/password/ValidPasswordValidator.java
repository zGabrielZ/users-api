package br.com.gabrielferreira.users.core.validations.password;

import br.com.gabrielferreira.users.core.utils.Constants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(password)) {
            return true;
        }

        List<String> errors = new ArrayList<>();

        if (!Constants.hasSpecialCharacter(password)) {
            errors.add("Password must contain at least one special character.");
        }

        if (!Constants.hasUppercaseCharacter(password)) {
            errors.add("Password must contain at least one uppercase letter.");
        }

        if (!Constants.hasLowercaseCharacter(password)) {
            errors.add("Password must contain at least one lowercase letter.");
        }

        if (!Constants.hasDigitCharacter(password)) {
            errors.add("Password must contain at least one digit.");
        }

        if (!errors.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();

            errors.forEach(error -> constraintValidatorContext
                    .buildConstraintViolationWithTemplate(error)
                    .addConstraintViolation());
            return false;
        }
        return true;
    }
}
