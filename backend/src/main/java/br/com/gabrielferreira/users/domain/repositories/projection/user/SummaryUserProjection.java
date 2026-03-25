package br.com.gabrielferreira.users.domain.repositories.projection.user;

import java.util.UUID;

public interface SummaryUserProjection {

    UUID getUserExternalId();

    String getEmail();

    UUID getProjectExternalId();

    UUID getDocumentExternalId();

    String getDocumentNumber();

    String getDocumentType();
}
