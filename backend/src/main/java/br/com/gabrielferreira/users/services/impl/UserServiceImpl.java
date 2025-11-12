package br.com.gabrielferreira.users.services.impl;

import br.com.gabrielferreira.users.entities.DocumentEntity;
import br.com.gabrielferreira.users.entities.UserEntity;
import br.com.gabrielferreira.users.enums.DocumentType;
import br.com.gabrielferreira.users.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.exceptions.UserNotFoundException;
import br.com.gabrielferreira.users.repositories.UserRepository;
import br.com.gabrielferreira.users.services.ProjectService;
import br.com.gabrielferreira.users.services.UserService;
import lombok.RequiredArgsConstructor;
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

    @Override
    public UserEntity update(UUID userExternalId, UserEntity roleEntity, UUID projectExternalId) {
        return null;
    }

    @Override
    public List<UserEntity> getAllUsers(UUID projectExternalId) {
        return List.of();
    }

    @Override
    public void delete(UUID userExternalId, UUID projectExternalId) {

    }
}
