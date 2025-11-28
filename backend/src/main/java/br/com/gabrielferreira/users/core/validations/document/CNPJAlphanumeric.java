package br.com.gabrielferreira.users.core.validations.document;

import jakarta.validation.Payload;
import org.hibernate.validator.constraints.br.CNPJ;

import java.lang.annotation.Annotation;

public class CNPJAlphanumeric implements CNPJ {

    @Override
    public String message() {
        return "{org.hibernate.validator.constraints.br.CNPJ.message}";
    }

    @Override
    public Class<?>[] groups() {
        return new Class[0];
    }

    @Override
    public Class<? extends Payload>[] payload() {
        return new Class[0];
    }

    @Override
    public Format format() {
        return Format.ALPHANUMERIC;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CNPJ.class;
    }
}
