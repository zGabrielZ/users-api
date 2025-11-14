package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.UserNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.UserRepository;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import br.com.gabrielferreira.users.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ProjectService projectService;

    @Transactional
    @Override
    public UserEntity save(UserEntity userEntity, UUID projectExternalId) {
        var project = projectService.getOneProject(projectExternalId);
        userEntity.setProject(project);

        var existingUserWithEmailAndProject = userRepository.findOneByEmailAndProject_ProjectExternalId(
                userEntity.getEmail(),
                projectExternalId
        );
        if (existingUserWithEmailAndProject.isPresent()) {
            throw new BusinessRuleException("Already exists a user with this email in the project");
        }

        var document = userEntity.getDocument();
        if (Objects.nonNull(document)) {
            var existingUserWithDocumentAndProject = userRepository.findOneUserByDocumentAndProjectExternalId(
                    document.getType(),
                    document.getNumber(),
                    projectExternalId
            );
            if (existingUserWithDocumentAndProject.isPresent()) {
                throw new BusinessRuleException("Already exists a user with this document in the project");
            }
        }

        // TODO:  criptografar a senha antes de salvar

        if (Objects.isNull(userEntity.getDocument())) {
            var documentEntity = DocumentEntity.builder()
                    .type(DocumentType.NONE)
                    .user(userEntity)
                    .build();
            userEntity.setDocument(documentEntity);
        }

        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity getOneUser(UUID userExternalId, UUID projectExternalId) {
        return userRepository.findOneByUserExternalIdAndProject_ProjectExternalId(userExternalId, projectExternalId)
                .orElseThrow(() -> new UserNotFoundException(userExternalId, projectExternalId));
    }

    @Transactional
    @Override
    public UserEntity update(UUID userExternalId, UserEntity userEntity, UUID projectExternalId) {
        var userFound = getOneUser(userExternalId, projectExternalId);

        userFound.setFirstName(userEntity.getFirstName());
        userFound.setLastName(userEntity.getLastName());
        return userRepository.save(userFound);
    }

    @Transactional
    @Override
    public UserEntity updateEmail(UUID userExternalId, String newEmail, UUID projectExternalId) {
        var userFound = getOneUser(userExternalId, projectExternalId);
        var existingUserWithEmailAndProject = userRepository.findOneByEmailAndProject_ProjectExternalId(
                newEmail,
                projectExternalId
        );

        if (existingUserWithEmailAndProject.isPresent() && !Objects.equals(userFound.getUserExternalId(), existingUserWithEmailAndProject.get().getUserExternalId())) {
            throw new BusinessRuleException("Already exists a user with this email in the project");
        }

        userFound.setEmail(newEmail);
        return userRepository.save(userFound);
    }

    @Transactional
    @Override
    public UserEntity updatePassword(UUID userExternalId, String oldPassword, String newPassword, UUID projectExternalId) {
        var userFound = getOneUser(userExternalId, projectExternalId);
        if (!StringUtils.equals(oldPassword, userFound.getPassword())) {
            throw new BusinessRuleException("Old password does not match");
        }

        userFound.setPassword(newPassword);
        return userRepository.save(userFound);
    }

    @Override
    public List<UserEntity> getAllUsers(UUID projectExternalId) {
        return List.of();
    }

    @Override
    public void delete(UUID userExternalId, UUID projectExternalId) {

    }
}
