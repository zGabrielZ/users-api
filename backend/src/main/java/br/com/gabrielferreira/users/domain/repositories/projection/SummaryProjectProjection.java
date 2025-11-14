package br.com.gabrielferreira.users.domain.repositories.projection;

import java.util.UUID;

public interface SummaryProjectProjection {

    UUID getProjectExternalId();

    String getName();
}
