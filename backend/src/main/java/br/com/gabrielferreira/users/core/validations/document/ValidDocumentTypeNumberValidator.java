package br.com.gabrielferreira.users.core.validations.document;

import br.com.gabrielferreira.users.domain.enums.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class ValidDocumentTypeNumberValidator implements ConstraintValidator<ValidDocumentTypeNumber, Object> {

    private String type;
    private String number;
    private String property;

    @Override
    public void initialize(ValidDocumentTypeNumber constraintAnnotation) {
        type = constraintAnnotation.type();
        number = constraintAnnotation.number();
        property = constraintAnnotation.property();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        DocumentType documentType = getDocumentTypeByProperty(object);
        String documentNumber = getNumberByProperty(object);
        if (DocumentType.isNone(documentType) && StringUtils.isNotBlank(documentNumber)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            ConstraintValidatorContext .ConstraintViolationBuilder builder = constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Document number must be null or empty when document type is NONE."
            );
            addPropertyNode(builder)
                    .addConstraintViolation();
            return false;
        }

        if (DocumentType.isCpf(documentType) && StringUtils.isBlank(documentNumber)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            ConstraintValidatorContext .ConstraintViolationBuilder builder = constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Document number must be provided when document type is CPF."
            );
            addPropertyNode(builder)
                    .addConstraintViolation();
            return false;
        }

        if (DocumentType.isCnpj(documentType) && StringUtils.isBlank(documentNumber)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            ConstraintValidatorContext .ConstraintViolationBuilder builder = constraintValidatorContext.buildConstraintViolationWithTemplate(
                    "Document number must be provided when document type is CNPJ."
            );
            addPropertyNode(builder)
                    .addConstraintViolation();
            return false;
        }

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

    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext addPropertyNode(ConstraintValidatorContext.ConstraintViolationBuilder builder) {
        if (StringUtils.isNotBlank(this.property)) {
            return builder.addPropertyNode(this.property);
        }
        return builder.addPropertyNode("number");
    }
}
