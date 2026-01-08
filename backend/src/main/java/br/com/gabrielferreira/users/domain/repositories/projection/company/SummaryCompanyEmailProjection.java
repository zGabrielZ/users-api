package br.com.gabrielferreira.users.domain.repositories.projection.company;

import java.util.UUID;

public interface SummaryCompanyEmailProjection {

    UUID getCompanyExternalId();

    String getEmail();
}
