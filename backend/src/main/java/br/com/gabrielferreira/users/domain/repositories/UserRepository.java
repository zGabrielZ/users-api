package br.com.gabrielferreira.users.domain.repositories;

import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findOneByEmailAndProject_ProjectExternalId(String email,  UUID projectExternalId);

    @Query(
            "SELECT u FROM UserEntity u " +
                    "LEFT JOIN FETCH u.document d " +
                    "WHERE d.type = :type " +
                    "AND d.number = :number " +
                    "AND u.project.projectExternalId = :projectExternalId"
    )
    Optional<UserEntity> findOneUserByDocumentAndProjectExternalId(@Param("type") DocumentType type, @Param("number") String number, @Param("projectExternalId") UUID projectExternalId);

    @Query(
            "SELECT u FROM UserEntity u " +
                    "LEFT JOIN FETCH u.document d " +
                    "WHERE u.userExternalId = :userExternalId " +
                    "AND u.project.projectExternalId = :projectExternalId"
    )
    Optional<UserEntity> findOneByUserExternalIdAndProject_ProjectExternalId(@Param("userExternalId") UUID userExternalId, @Param("projectExternalId") UUID projectExternalId);
}
