package br.com.gabrielferreira.users.domain.exceptions;

import java.io.Serial;

public class BusinessRuleException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -9054990262780475095L;

    public BusinessRuleException(String message) {
        super(message);
    }
}
