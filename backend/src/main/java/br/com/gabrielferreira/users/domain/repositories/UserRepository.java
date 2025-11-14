package br.com.gabrielferreira.users.domain.repositories;

import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.domain.repositories.projection.SummaryUserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(
            "SELECT u.userExternalId AS userExternalId, u.email AS email, p.projectExternalId AS projectExternalId " +
                    "FROM UserEntity u " +
                    "JOIN u.project p " +
                    "WHERE u.email = :email " +
                    "AND p.projectExternalId = :projectExternalId"
    )
    Optional<SummaryUserProjection> findOneByEmailAndProject_ProjectExternalId(@Param("email") String email, @Param("projectExternalId") UUID projectExternalId);

    @Query(
            "SELECT u.userExternalId AS userExternalId, u.email AS email, p.projectExternalId AS projectExternalId, " +
                    "d.type as documentType, d.number as number, d.documentExternalId as documentExternalId FROM UserEntity u " +
                    "LEFT JOIN u.document d " +
                    "JOIN u.project p " +
                    "WHERE d.type = :type " +
                    "AND d.number = :number " +
                    "AND p.projectExternalId = :projectExternalId"
    )
    Optional<SummaryUserProjection> findOneUserByDocumentAndProjectExternalId(@Param("type") DocumentType type, @Param("number") String number, @Param("projectExternalId") UUID projectExternalId);

    @Query(
            "SELECT u FROM UserEntity u " +
                    "LEFT JOIN FETCH u.document d " +
                    "WHERE u.userExternalId = :userExternalId " +
                    "AND u.project.projectExternalId = :projectExternalId"
    )
    Optional<UserEntity> findOneByUserExternalIdAndProject_ProjectExternalId(@Param("userExternalId") UUID userExternalId, @Param("projectExternalId") UUID projectExternalId);
}
