package br.com.gabrielferreira.users.services;

import br.com.gabrielferreira.users.entities.RoleEntity;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    RoleEntity save(RoleEntity roleEntity, UUID projectExternalId);

    RoleEntity getOneRole(UUID roleExternalId, UUID projectExternalId);

    RoleEntity update(UUID roleExternalId, RoleEntity roleEntity, UUID projectExternalId);

    List<RoleEntity> getAllRoles(UUID projectExternalId);

    void delete(UUID roleExternalId, UUID projectExternalId);
}
