package br.com.gabrielferreira.users.services.impl;

import br.com.gabrielferreira.users.entities.RoleEntity;
import br.com.gabrielferreira.users.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.exceptions.RoleNotFoundException;
import br.com.gabrielferreira.users.repositories.RoleRepository;
import br.com.gabrielferreira.users.services.ProjectService;
import br.com.gabrielferreira.users.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final ProjectService projectService;

    @Override
    @Transactional
    public RoleEntity save(RoleEntity roleEntity, UUID projectExternalId) {
        var project = projectService.getOneProject(projectExternalId);
        roleEntity.setProject(project);

        var roleWithAuthority = roleRepository.findOneByAuthorityAndProject_ProjectExternalId(roleEntity.getAuthority(), projectExternalId);
        if (roleWithAuthority.isPresent()) {
            String message = String.format("A role with the authority '%s' already exists in the project '%s'.",
                    roleEntity.getAuthority(), project.getName());
            throw new BusinessRuleException(message);
        }

        return roleRepository.save(roleEntity);
    }

    @Override
    public RoleEntity getOneRole(UUID roleExternalId, UUID projectExternalId) {
        return roleRepository.findOneByRoleExternalIdAndProject_ProjectExternalId(roleExternalId, projectExternalId)
                .orElseThrow(() -> new RoleNotFoundException(roleExternalId, projectExternalId));
    }

    @Override
    @Transactional
    public RoleEntity update(UUID roleExternalId, RoleEntity roleEntity, UUID projectExternalId) {
        var project = projectService.getOneProject(projectExternalId);
        var role = getOneRole(roleExternalId, projectExternalId);

        var roleWithAuthority = roleRepository.findOneByAuthorityAndProject_ProjectExternalId(roleEntity.getAuthority(), projectExternalId);
        if (roleWithAuthority.isPresent() && !Objects.equals(roleWithAuthority.get().getId(), role.getId())) {
            String message = String.format("A role with the authority '%s' already exists in the project '%s'.",
                    roleEntity.getAuthority(), project.getName());
            throw new BusinessRuleException(message);
        }

        role.setDescription(roleEntity.getDescription());
        role.setAuthority(roleEntity.getAuthority());
        return roleRepository.save(role);
    }

    @Override
    public List<RoleEntity> getAllRoles(UUID projectExternalId) {
        return roleRepository.findAllByProject_ProjectExternalId(projectExternalId);
    }

    @Transactional
    @Override
    public void delete(UUID roleExternalId, UUID projectExternalId) {
        var project = projectService.getOneProject(projectExternalId);
        var roleFound = getOneRole(roleExternalId, project.getProjectExternalId());

        try {
            roleRepository.delete(roleFound);
            roleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("Role with ID %s cannot be removed as it is in use.".formatted(roleExternalId));
        }
    }
}
