package br.com.gabrielferreira.users.domain.exceptions;

import java.io.Serial;
import java.util.UUID;

public class CompanyNotFoundException extends EntityNotFoundException {

    @Serial
    private static final long serialVersionUID = -785495297308597136L;

    protected CompanyNotFoundException(String message) {
        super(message);
    }

    public CompanyNotFoundException(UUID companyId, UUID projectId) {
        this("Company with ID %s not found for Project with ID %s".formatted(companyId, projectId));
    }
}
