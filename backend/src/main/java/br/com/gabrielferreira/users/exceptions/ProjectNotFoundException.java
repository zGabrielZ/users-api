package br.com.gabrielferreira.users.exceptions;

import java.io.Serial;
import java.util.UUID;

public class ProjectNotFoundException extends EntityNotFoundException {

    @Serial
    private static final long serialVersionUID = -785495297308597136L;

    protected ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(UUID projectId) {
        this("Project with ID %s not found".formatted(projectId));
    }
}
