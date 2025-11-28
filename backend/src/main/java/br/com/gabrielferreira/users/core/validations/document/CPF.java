package br.com.gabrielferreira.users.core.validations.document;

import jakarta.validation.Payload;

import java.lang.annotation.Annotation;

public class CPF implements org.hibernate.validator.constraints.br.CPF {

    @Override
    public String message() {
        return "{org.hibernate.validator.constraints.br.CPF.message}";
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
    public Class<? extends Annotation> annotationType() {
        return CPF.class;
    }
}
