package br.com.gabrielferreira.users.validations.document;

import br.com.gabrielferreira.users.enums.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class ValidDocumentValidator implements ConstraintValidator<ValidDocument, Object> {

    private String type;
    private String number;

    @Override
    public void initialize(ValidDocument constraintAnnotation) {
        type = constraintAnnotation.type();
        number = constraintAnnotation.number();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isAllBlank(type, number)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Document type and number cannot be blank.")
                    .addConstraintViolation();
            return false;
        }

        DocumentType documentType = getDocumentTypeByProperty(object);
        String documentNumber = getNumberByProperty(object);
        if (Objects.isNull(documentType) || StringUtils.isBlank(documentNumber)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Document type and number cannot be blank.")
                    .addConstraintViolation();
            return false;
        }

        // TODO: Validar número do documento conforme o tipo

        return true;
    }

    private String getNumberByProperty(Object object) {
        if (StringUtils.isBlank(number)) {
            return number;
        }

        var descriptor = BeanUtils.getPropertyDescriptor(object.getClass(), number);
        if (Objects.isNull(descriptor) || Objects.isNull(descriptor.getReadMethod())) {
            return null;
        }

        try {
            return (String) descriptor.getReadMethod().invoke(object);
        } catch (Exception e) {
            return null;
        }
    }

    private DocumentType getDocumentTypeByProperty(Object object) {
        if (StringUtils.isBlank(type)) {
            return null;
        }

        var descriptor = BeanUtils.getPropertyDescriptor(object.getClass(), type);
        if (Objects.isNull(descriptor) || Objects.isNull(descriptor.getReadMethod())) {
            return null;
        }

        try {
            return (DocumentType) descriptor.getReadMethod().invoke(object);
        } catch (Exception e) {
            return null;
        }
    }
}
