package br.com.gabrielferreira.users.domain.exceptions;

import java.io.Serial;

public class EntityInUseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3765053401215599604L;

    public EntityInUseException(String message) {
        super(message);
    }
}
