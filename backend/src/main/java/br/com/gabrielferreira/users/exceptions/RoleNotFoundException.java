package br.com.gabrielferreira.users.exceptions;

import java.io.Serial;
import java.util.UUID;

public class RoleNotFoundException extends EntityNotFoundException {

    @Serial
    private static final long serialVersionUID = -785495297308597136L;

    protected RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(UUID roleId, UUID projectId) {
        this("Role with ID %s not found for Project with ID %s".formatted(roleId, projectId));
    }
}
