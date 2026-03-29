package br.com.gabrielferreira.users.domain.services;

import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.user.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserEntity save(UserEntity userEntity, UUID projectExternalId);

    UserEntity getOneUser(UUID userExternalId, UUID projectExternalId);

    UserEntity update(UUID userExternalId, UserEntity userEntity, UUID projectExternalId);

    UserEntity updateDocument(UUID userExternalId, DocumentEntity documentEntity, UUID projectExternalId);

    UserEntity updateEmail(UUID userExternalId, String newEmail, UUID projectExternalId);

    UserEntity updatePassword(UUID userExternalId, String oldPassword,
                                                   String newPassword, UUID projectExternalId);

    Page<UserEntity> getAllUsers(UUID projectExternalId, UserFilter userFilter, Pageable pageable);

    void delete(UUID userExternalId, UUID projectExternalId);
}
