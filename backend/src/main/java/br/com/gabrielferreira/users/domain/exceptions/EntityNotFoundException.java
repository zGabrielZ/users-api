package br.com.gabrielferreira.users.domain.exceptions;

import java.io.Serial;

public abstract class EntityNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3861748605867641899L;

    protected EntityNotFoundException(String message) {
        super(message);
    }
}
