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
        var existingProjectWithName = projectRepository.findOneByName(projectEntity.getName());
        if (existingProjectWithName.isPresent()) {
            throw new BusinessRuleException("A project with the name '%s' already exists.".formatted(projectEntity.getName()));
        }
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
        var projectFound = getOneProject(projectExternalId);
        var existingProjectWithName = projectRepository.findOneByName(projectEntity.getName());
        if (existingProjectWithName.isPresent() && !Objects.equals(projectFound.getProjectExternalId(), existingProjectWithName.get().getProjectExternalId())) {
            throw new BusinessRuleException("A project with the name '%s' already exists.".formatted(projectEntity.getName()));
        }

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
        var projectFound = getOneProject(projectExternalId);
        try {
            projectRepository.delete(projectFound);
            projectRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new EntityInUseException("Project with ID %s cannot be removed as it is in use.".formatted(projectExternalId));
        }
    }
}
