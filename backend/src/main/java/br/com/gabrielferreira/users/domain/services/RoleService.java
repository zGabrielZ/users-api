package br.com.gabrielferreira.users.domain.services;

import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.RoleFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleEntity save(RoleEntity roleEntity, UUID projectExternalId);

    RoleEntity getOneRole(UUID roleExternalId, UUID projectExternalId);

    RoleEntity update(UUID roleExternalId, RoleEntity roleEntity, UUID projectExternalId);

    Page<RoleEntity> getAllRoles(RoleFilter filter, Pageable pageable);

    void delete(UUID roleExternalId, UUID projectExternalId);
}
