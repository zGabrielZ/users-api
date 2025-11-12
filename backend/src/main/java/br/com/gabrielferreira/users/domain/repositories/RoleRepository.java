package br.com.gabrielferreira.users.domain.repositories;

import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    @Query(
            "SELECT r FROM RoleEntity r " +
                    "JOIN FETCH r.project p " +
                    "WHERE r.roleExternalId = :roleExternalId " +
                    "AND p.projectExternalId = :projectExternalId"
    )
    Optional<RoleEntity> findOneByRoleExternalIdAndProject_ProjectExternalId(@Param("roleExternalId") UUID roleExternalId, @Param("projectExternalId") UUID projectExternalId);

    Optional<RoleEntity> findOneByAuthorityAndProject_ProjectExternalId(String authority, UUID projectExternalId);

    @Query(
            "SELECT r FROM RoleEntity r " +
                    "JOIN FETCH r.project p " +
                    "WHERE p.projectExternalId = :projectExternalId"
    )
    List<RoleEntity> findAllByProject_ProjectExternalId(@Param("projectExternalId") UUID projectExternalId);
}
