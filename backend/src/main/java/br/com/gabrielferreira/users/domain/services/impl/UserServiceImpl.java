package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.UserNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.UserRepository;
import br.com.gabrielferreira.users.domain.repositories.projection.SummaryUserProjection;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import br.com.gabrielferreira.users.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ProjectService projectService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserEntity save(UserEntity userEntity, UUID projectExternalId) {
        ProjectEntity project = projectService.getOneProject(projectExternalId);
        userEntity.setProject(project);

        validateExistingUserWithEmailAndProject(userEntity.getEmail(), projectExternalId);
        applyDefaultDocumentValues(userEntity);

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
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
        UserEntity userFound = getOneUser(userExternalId, projectExternalId);

        userFound.setFirstName(userEntity.getFirstName());
        userFound.setLastName(userEntity.getLastName());
        return userRepository.save(userFound);
    }

    @Transactional
    @Override
    public UserEntity updateDocument(UUID userExternalId, DocumentEntity documentEntity, UUID projectExternalId) {
        UserEntity userFound = getOneUser(userExternalId, projectExternalId);
        DocumentEntity document = userFound.getDocument();
        documentEntity.setNumber(Mask.documentWithoutMask(documentEntity.getType(), documentEntity.getNumber()));

        validateExistingUserWithDocument(document);
        validateExistingUserWithDocumentAndProject(documentEntity, projectExternalId);

        document.setType(documentEntity.getType());
        document.setNumber(documentEntity.getNumber());
        userFound.setDocument(document);
        return userRepository.save(userFound);
    }

    // TODO: deve criar tres colunas (email pendente, email status pedennte, e codigo), deve encaminhar uma mensagem no topico e fazer o envio de notificacao de email mostrando o codigo
    @Transactional
    @Override
    public UserEntity updateEmail(UUID userExternalId, String newEmail, UUID projectExternalId) {
        UserEntity userFound = getOneUser(userExternalId, projectExternalId);
        Optional<SummaryUserProjection> existingUserWithEmailAndProject = userRepository.findOneByEmailAndProject_ProjectExternalId(
                newEmail,
                projectExternalId
        );

        if (existingUserWithEmailAndProject.isPresent() && !Objects.equals(userFound.getUserExternalId(), existingUserWithEmailAndProject.get().getUserExternalId())) {
            throw new BusinessRuleException("Already exists a user with this email in the project");
        }

        userFound.setEmail(newEmail);
        return userRepository.save(userFound);
    }

    // TODO: deve criar tres colunas (email pendente, email status pedennte, e codigo), deve encaminhar uma mensagem no topico e fazer o envio de notificacao de email mostrando o codigo
    @Transactional
    @Override
    public UserEntity updatePassword(UUID userExternalId, String oldPassword, String newPassword, UUID projectExternalId) {
        UserEntity userFound = getOneUser(userExternalId, projectExternalId);
        if (!StringUtils.equals(oldPassword, userFound.getPassword())) {
            throw new BusinessRuleException("Old password does not match");
        }

        userFound.setPassword(newPassword);
        return userRepository.save(userFound);
    }

    // TODO: deve criar um outro endpoint que valida o codigo enviado por email e atualiza o email do usuario

    // TODO: implement pagination
    @Override
    public List<UserEntity> getAllUsers(UUID projectExternalId) {
        return Collections.emptyList();
    }

    @Override
    public void delete(UUID userExternalId, UUID projectExternalId) {
        UserEntity userFound = getOneUser(userExternalId, projectExternalId);
        try {
            userRepository.delete(userFound);
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException("User with ID %s cannot be removed as it is in use.".formatted(userExternalId));
        }
    }

    private void validateExistingUserWithEmailAndProject(String email, UUID projectExternalId) {
        Optional<SummaryUserProjection> existingUserWithEmailAndProject = userRepository.findOneByEmailAndProject_ProjectExternalId(
                email,
                projectExternalId
        );
        if (existingUserWithEmailAndProject.isPresent()) {
            throw new BusinessRuleException("Already exists a user with this email in the project");
        }
    }

    private void validateExistingUserWithDocumentAndProject(DocumentEntity document, UUID projectExternalId) {
        Optional<SummaryUserProjection> existingUserWithDocumentAndProject = userRepository.findOneUserByDocumentAndProjectExternalId(
                document.getType(),
                document.getNumber(),
                projectExternalId
        );
        if (existingUserWithDocumentAndProject.isPresent()) {
            throw new BusinessRuleException("Already exists a user with this document in the project");
        }
    }

    private void applyDefaultDocumentValues(UserEntity userEntity) {
        if (Objects.isNull(userEntity.getDocument())) {
            var documentEntity = DocumentEntity.builder()
                    .type(DocumentType.NONE)
                    .user(userEntity)
                    .build();
            userEntity.setDocument(documentEntity);
        }
    }

    private void validateExistingUserWithDocument(DocumentEntity document) {
        if (Objects.nonNull(document) && StringUtils.isNotBlank(document.getNumber())) {
            throw new BusinessRuleException("User already has a document registered");
        }
    }
}
