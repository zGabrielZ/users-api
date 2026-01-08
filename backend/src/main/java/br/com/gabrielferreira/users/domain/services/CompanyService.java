package br.com.gabrielferreira.users.domain.services;

import br.com.gabrielferreira.users.domain.entities.CompanyEntity;

import java.util.UUID;

public interface CompanyService {

    CompanyEntity save(CompanyEntity companyEntity, UUID externalProjectId);
}
