package br.com.gabrielferreira.users.domain.services;

import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.RoleFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserRoleService {

    void associateRole(UUID userExternalId, UUID roleExternalId, UUID projectExternalId);

    void disassociateRole(UUID userExternalId, UUID roleExternalId, UUID projectExternalId);

    Page<RoleEntity> listRolesByUser(UUID userExternalId, Pageable pageable, RoleFilter filter);
}
