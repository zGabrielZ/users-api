package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.EntityInUseException;
import br.com.gabrielferreira.users.domain.exceptions.ProjectNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.ProjectRepository;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public List<ProjectEntity> getAllProjects() {
        return projectRepository.findAll();
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
