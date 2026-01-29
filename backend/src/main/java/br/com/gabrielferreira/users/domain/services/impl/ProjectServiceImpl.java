package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.EntityInUseException;
import br.com.gabrielferreira.users.domain.exceptions.ProjectNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.ProjectRepository;
import br.com.gabrielferreira.users.domain.repositories.filter.ProjectFilter;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import br.com.gabrielferreira.users.domain.specs.ProjectSpec;
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
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    @Override
    public ProjectEntity save(ProjectEntity projectEntity) {
        validateExistingProjectName(projectEntity.getName(), null);
        return projectRepository.save(projectEntity);
    }

    @Override
    public ProjectEntity getOneProject(UUID projectExternalId) {
        return projectRepository.findOneByProjectExternalId(projectExternalId)
                .orElseThrow(() -> new ProjectNotFoundException(projectExternalId));
    }

    @Transactional
    @Override
    public ProjectEntity update(UUID projectExternalId, ProjectEntity projectEntity) {
        ProjectEntity projectFound = getOneProject(projectExternalId);
        validateExistingProjectName(projectEntity.getName(), projectExternalId);

        projectFound.setName(projectEntity.getName());
        return projectRepository.save(projectFound);
    }

    @Override
    public Page<ProjectEntity> getAllProjects(ProjectFilter filter, Pageable pageable) {
        return projectRepository.findAll(ProjectSpec.usingFilter(filter), pageable);
    }

    @Transactional
    @Override
    public void delete(UUID projectExternalId) {
        ProjectEntity projectFound = getOneProject(projectExternalId);
        try {
            projectRepository.delete(projectFound);
            projectRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException(String.format("Project with ID %s cannot be removed as it is in use.", projectExternalId));
        }
    }

    private void validateExistingProjectName(String name, UUID projectExternalId) {
        String templateErrorMessage = "A project with the name '%s' already exists.";
        projectRepository.findOneByName(name)
                .ifPresent(project -> {
                    if (Objects.isNull(projectExternalId)) {
                        throw new BusinessRuleException(templateErrorMessage.formatted(name));
                    }

                    if (!Objects.equals(projectExternalId, project.getProjectExternalId())) {
                        throw new BusinessRuleException(templateErrorMessage.formatted(name));
                    }
                });
    }
}
