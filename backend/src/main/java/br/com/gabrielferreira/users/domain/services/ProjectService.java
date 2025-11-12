package br.com.gabrielferreira.users.domain.services;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectEntity save(ProjectEntity projectEntity);

    ProjectEntity getOneProject(UUID projectExternalId);

    ProjectEntity update(UUID projectExternalId, ProjectEntity projectEntity);

    List<ProjectEntity> getAllProjects();

    void delete(UUID projectExternalId);
}
