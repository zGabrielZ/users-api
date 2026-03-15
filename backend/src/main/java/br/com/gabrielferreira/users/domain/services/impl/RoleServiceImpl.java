package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.EntityInUseException;
import br.com.gabrielferreira.users.domain.exceptions.RoleNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.RoleRepository;
import br.com.gabrielferreira.users.domain.repositories.filter.role.RoleFilter;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import br.com.gabrielferreira.users.domain.services.RoleService;
import br.com.gabrielferreira.users.domain.specs.RoleSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        ProjectEntity project = projectService.getOneProject(projectExternalId);
        roleEntity.setProject(project);

        validateExistingAuthorityRoleForProject(roleEntity.getAuthority(), project, null);
        return roleRepository.save(roleEntity);
    }

    @Override
    public RoleEntity getOneRole(UUID roleExternalId, UUID projectExternalId) {
        return roleRepository.findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId)
                .orElseThrow(() -> new RoleNotFoundException(roleExternalId, projectExternalId));
    }

    @Override
    @Transactional
    public RoleEntity update(UUID roleExternalId, RoleEntity roleEntity, UUID projectExternalId) {
        ProjectEntity project = projectService.getOneProject(projectExternalId);
        RoleEntity role = getOneRole(roleExternalId, projectExternalId);
        validateExistingAuthorityRoleForProject(roleEntity.getAuthority(), project, role.getRoleExternalId());

        role.setDescription(roleEntity.getDescription());
        role.setAuthority(roleEntity.getAuthority());
        return roleRepository.save(role);
    }

    @Override
    public Page<RoleEntity> getAllRoles(RoleFilter filter, Pageable pageable) {
        return roleRepository.findAll(RoleSpec.usingFilter(filter), pageable);
    }

    @Transactional
    @Override
    public void delete(UUID roleExternalId, UUID projectExternalId) {
        ProjectEntity project = projectService.getOneProject(projectExternalId);
        RoleEntity roleFound = getOneRole(roleExternalId, project.getProjectExternalId());

        try {
            roleRepository.delete(roleFound);
            roleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(String.format("Role with ID %s cannot be removed as it is in use.", roleExternalId));
        }
    }

    private void validateExistingAuthorityRoleForProject(String authority, ProjectEntity projectEntity, UUID roleExternalId) {
        roleRepository.findOneByAuthorityAndProjectExternalId(authority, projectEntity.getProjectExternalId())
                .ifPresent(role -> {
                    String templateErrorMessage = String.format("A role with the authority '%s' already exists in the project '%s'.", authority, projectEntity.getName());
                    if (Objects.isNull(roleExternalId)) {
                        throw new BusinessRuleException(templateErrorMessage);
                    }

                    if (!Objects.equals(roleExternalId, role.getRoleExternalId())) {
                        throw new BusinessRuleException(templateErrorMessage);
                    }
                });
    }
}
