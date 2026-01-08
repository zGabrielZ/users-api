package br.com.gabrielferreira.users.domain.repositories.projection.company;

import java.util.UUID;

public interface SummaryCompanyDocumentProjection {

    UUID getCompanyExternalId();

    UUID getDocumentExternalId();

    String getDocumentNumber();

    String getDocumentType();
}
