package br.com.gabrielferreira.users.domain.services;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.ProjectFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectService {

    ProjectEntity save(ProjectEntity projectEntity);

    ProjectEntity getOneProject(UUID projectExternalId);

    ProjectEntity update(UUID projectExternalId, ProjectEntity projectEntity);

    Page<ProjectEntity> getAllProjects(ProjectFilter filter, Pageable pageable);

    void delete(UUID projectExternalId);
}
