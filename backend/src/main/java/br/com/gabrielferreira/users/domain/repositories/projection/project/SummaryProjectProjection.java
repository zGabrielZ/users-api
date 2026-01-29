package br.com.gabrielferreira.users.domain.repositories.projection.project;

import java.util.UUID;

public interface SummaryProjectProjection {

    UUID getProjectExternalId();

    String getName();
}
