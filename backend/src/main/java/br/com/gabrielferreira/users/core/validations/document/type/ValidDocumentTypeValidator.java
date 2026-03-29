package br.com.gabrielferreira.users.core.validations.document.type;

import br.com.gabrielferreira.users.domain.enums.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public class ValidDocumentTypeValidator implements ConstraintValidator<ValidDocumentType, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(value)) {
            return true;
        }

        Optional<DocumentType> existingDocumentType = Arrays.stream(DocumentType.values())
                .filter(type -> type.name().equals(value))
                .findFirst();
        if (existingDocumentType.isEmpty()) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Invalid document type. Allowed values are: " + Arrays.toString(DocumentType.values())
            ).addConstraintViolation();
            return false;
        }
        return true;
    }
}
