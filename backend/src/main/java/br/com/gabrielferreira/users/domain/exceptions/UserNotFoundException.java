package br.com.gabrielferreira.users.domain.exceptions;

import java.io.Serial;
import java.util.UUID;

public class UserNotFoundException extends EntityNotFoundException {

    @Serial
    private static final long serialVersionUID = -785495297308597136L;

    protected UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(UUID userId, UUID projectId) {
        this(String.format("User with ID %s not found for Project with ID %s", userId, projectId));
    }
}
