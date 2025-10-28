package br.com.gabrielferreira.users.repositories;

import br.com.gabrielferreira.users.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findOneByProjectExternalId(java.util.UUID projectExternalId);
}
