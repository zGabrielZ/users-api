package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.services.RoleService;
import br.com.gabrielferreira.users.domain.services.UserRoleService;
import br.com.gabrielferreira.users.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserService userService;

    private final RoleService roleService;

    @Transactional
    @Override
    public void associateRole(UUID userExternalId, UUID roleExternalId, UUID projectExternalId) {
        var userFound = userService.getOneUser(userExternalId, projectExternalId);
        var roleFound = roleService.getOneRole(roleExternalId, projectExternalId);

        if (!hasRoleAssignedToUser(roleFound, userFound.getRoles())) {
            userFound.getRoles().add(roleFound);
        }
    }

    @Transactional
    @Override
    public void disassociateRole(UUID userExternalId, UUID roleExternalId, UUID projectExternalId) {
        var userFound = userService.getOneUser(userExternalId, projectExternalId);
        var roleFound = roleService.getOneRole(roleExternalId, projectExternalId);

        if (hasRoleAssignedToUser(roleFound, userFound.getRoles())) {
            userFound.getRoles().removeIf(role -> role.getRoleExternalId().equals(roleFound.getRoleExternalId()));
        }
    }

    // TODO: Implementar listRolesByUser
    @Override
    public Page<RoleEntity> listRolesByUser(UUID userExternalId, UUID roleExternalId, UUID projectExternalId, Pageable pageable) {
        return null;
    }

    private boolean hasRoleAssignedToUser(RoleEntity roleEntity, List<RoleEntity> roleEntities) {
        if (CollectionUtils.isEmpty(roleEntities)) {
            return false;
        }
        return roleEntities.stream()
                .anyMatch(role -> role.getRoleExternalId().equals(roleEntity.getRoleExternalId()));
    }
}
