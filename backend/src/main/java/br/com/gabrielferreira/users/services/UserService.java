package br.com.gabrielferreira.users.services;

import br.com.gabrielferreira.users.entities.UserEntity;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserEntity save(UserEntity userEntity, UUID projectExternalId);

    UserEntity getOneUser(UUID userExternalId, UUID projectExternalId);

    UserEntity update(UUID userExternalId, UserEntity roleEntity, UUID projectExternalId);

    List<UserEntity> getAllUsers(UUID projectExternalId);

    void delete(UUID userExternalId, UUID projectExternalId);
}
