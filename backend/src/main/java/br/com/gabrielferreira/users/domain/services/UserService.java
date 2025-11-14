package br.com.gabrielferreira.users.domain.services;

import br.com.gabrielferreira.users.domain.entities.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserEntity save(UserEntity userEntity, UUID projectExternalId);

    UserEntity getOneUser(UUID userExternalId, UUID projectExternalId);

    UserEntity update(UUID userExternalId, UserEntity userEntity, UUID projectExternalId);

    UserEntity updateEmail(UUID userExternalId, String newEmail, UUID projectExternalId);

    UserEntity updatePassword(UUID userExternalId, String oldPassword,
                                                   String newPassword, UUID projectExternalId);

    List<UserEntity> getAllUsers(UUID projectExternalId);

    void delete(UUID userExternalId, UUID projectExternalId);
}
