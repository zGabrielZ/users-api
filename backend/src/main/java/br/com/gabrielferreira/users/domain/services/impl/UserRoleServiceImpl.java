package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.repositories.RoleRepository;
import br.com.gabrielferreira.users.domain.repositories.filter.RoleFilter;
import br.com.gabrielferreira.users.domain.services.RoleService;
import br.com.gabrielferreira.users.domain.services.UserRoleService;
import br.com.gabrielferreira.users.domain.services.UserService;
import br.com.gabrielferreira.users.domain.specs.UserRoleSpec;
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

    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public void associateRole(UUID userExternalId, UUID roleExternalId, UUID projectExternalId) {
        UserEntity userFound = userService.getOneUser(userExternalId, projectExternalId);
        RoleEntity roleFound = roleService.getOneRole(roleExternalId, projectExternalId);

        if (!hasRoleAssignedToUser(roleFound, userFound.getRoles())) {
            userFound.getRoles().add(roleFound);
        }
    }

    @Transactional
    @Override
    public void disassociateRole(UUID userExternalId, UUID roleExternalId, UUID projectExternalId) {
        UserEntity userFound = userService.getOneUser(userExternalId, projectExternalId);
        RoleEntity roleFound = roleService.getOneRole(roleExternalId, projectExternalId);

        if (hasRoleAssignedToUser(roleFound, userFound.getRoles())) {
            userFound.getRoles().removeIf(role -> role.getRoleExternalId().equals(roleFound.getRoleExternalId()));
        }
    }

    @Override
    public Page<RoleEntity> listRolesByUser(UUID userExternalId, Pageable pageable, RoleFilter filter) {
        return roleRepository.findAll(
                new UserRoleSpec(userExternalId, filter),
                pageable
        );
    }

    private boolean hasRoleAssignedToUser(RoleEntity roleEntity, List<RoleEntity> roleEntities) {
        if (CollectionUtils.isEmpty(roleEntities)) {
            return false;
        }
        return roleEntities.stream()
                .anyMatch(role -> role.getRoleExternalId().equals(roleEntity.getRoleExternalId()));
    }
}
