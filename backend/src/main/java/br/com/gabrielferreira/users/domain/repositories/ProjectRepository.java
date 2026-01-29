package br.com.gabrielferreira.users.domain.repositories;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.repositories.projection.project.SummaryProjectProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long>, JpaSpecificationExecutor<ProjectEntity> {

    Optional<ProjectEntity> findOneByProjectExternalId(UUID projectExternalId);

    @Query(
            "SELECT p.projectExternalId as projectExternalId, p.name as name FROM ProjectEntity p WHERE p.name = :name"
    )
    Optional<SummaryProjectProjection> findOneByName(@Param("name") String name);
}
