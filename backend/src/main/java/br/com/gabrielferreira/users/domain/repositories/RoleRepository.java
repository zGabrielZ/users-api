package br.com.gabrielferreira.users.domain.repositories;

import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.repositories.projection.SummaryRoleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>, JpaSpecificationExecutor<RoleEntity> {

    @Query(
            "SELECT r FROM RoleEntity r " +
                    "JOIN FETCH r.project p " +
                    "WHERE r.roleExternalId = :roleExternalId " +
                    "AND p.projectExternalId = :projectExternalId"
    )
    Optional<RoleEntity> findOneByRoleExternalIdAndProject_ProjectExternalId(@Param("roleExternalId") UUID roleExternalId, @Param("projectExternalId") UUID projectExternalId);

    @Query(
            "SELECT r.roleExternalId as roleExternalId, r.authority as authority FROM RoleEntity r " +
                    "JOIN r.project p " +
                    "WHERE r.authority = :authority " +
                    "AND p.projectExternalId = :projectExternalId"
    )
    Optional<SummaryRoleProjection> findOneByAuthorityAndProject_ProjectExternalId(@Param("authority") String authority, @Param("projectExternalId") UUID projectExternalId);
}
