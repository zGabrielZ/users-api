package br.com.gabrielferreira.users.core.validations.document;

import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

public class ValidDocumentValidator implements ConstraintValidator<ValidDocument, Object> {

    private String type;
    private String number;
    private String property;
    private CPFValidator cpfValidator;
    private CNPJValidator cnpjValidator;

    @Override
    public void initialize(ValidDocument constraintAnnotation) {
        type = constraintAnnotation.type();
        number = constraintAnnotation.number();
        property = constraintAnnotation.property();
        cpfValidator = new CPFValidator();
        cpfValidator.initialize(new CPFNumeric());
        cnpjValidator = new CNPJValidator();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        DocumentType documentType = getDocumentTypeByProperty(object);
        String documentNumber = getNumberByProperty(object);
        if (Objects.isNull(documentType) || StringUtils.isBlank(documentNumber)) {
            return true;
        }

        return switch (documentType) {
            case CPF -> isCpfValid(documentNumber, constraintValidatorContext);
            case CNPJ  -> isCnpjValid(documentType, documentNumber, constraintValidatorContext);
            default -> true;
        };
    }

    private boolean isCnpjValid(DocumentType documentType, String documentNumber, ConstraintValidatorContext constraintValidatorContext) {
        boolean isOnlyNumeric = Mask.isOnlyNumeric(documentType, documentNumber);
        if (isOnlyNumeric) {
            cnpjValidator.initialize(new CNPJNumeric());
        } else {
            cnpjValidator.initialize(new CNPJAlphanumeric());
        }

        if (!cnpjValidator.isValid(documentNumber, constraintValidatorContext)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            ConstraintValidatorContext.ConstraintViolationBuilder builder =
                    constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "Invalid CNPJ number."
                    );

            addPropertyNode(builder)
                    .addConstraintViolation();
            return false;
        }

        if (isOnlyNumeric && Mask.isSequential(documentNumber)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            ConstraintValidatorContext.ConstraintViolationBuilder builder =
                    constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "CNPJ number cannot be sequential digits."
                    );
            addPropertyNode(builder)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean isCpfValid(String documentNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (!cpfValidator.isValid(documentNumber, constraintValidatorContext)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            ConstraintValidatorContext .ConstraintViolationBuilder builder =
                    constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "Invalid CPF number."
                    );
            addPropertyNode(builder)
                    .addConstraintViolation();
            return false;
        }

        if (Mask.isSequential(documentNumber)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            ConstraintValidatorContext.ConstraintViolationBuilder builder =
                    constraintValidatorContext.buildConstraintViolationWithTemplate(
                            "CPF number cannot be sequential digits."
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
