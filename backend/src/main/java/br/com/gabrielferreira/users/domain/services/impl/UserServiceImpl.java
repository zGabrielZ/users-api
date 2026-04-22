package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.UserNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.UserRepository;
import br.com.gabrielferreira.users.domain.repositories.filter.user.UserFilter;
import br.com.gabrielferreira.users.domain.repositories.projection.user.SummaryUserProjection;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import br.com.gabrielferreira.users.domain.services.UserService;
import br.com.gabrielferreira.users.domain.specs.UserSpec;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

        validateExistingUserWithEmail(userEntity.getEmail(), projectExternalId, null);
        applyDefaultDocumentValues(userEntity);

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity getOneUser(UUID userExternalId, UUID projectExternalId) {
        return userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId)
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
        DocumentEntity documentFound = userFound.getDocument();

        documentEntity.setNumber(Mask.documentWithoutMask(documentEntity.getType(), documentEntity.getNumber()));
        validateExistingUserWithDocument(documentFound);
        validateExistingUserWithDocumentAndProject(documentEntity, projectExternalId);

        documentFound.setType(documentEntity.getType());
        documentFound.setNumber(documentEntity.getNumber());
        userFound.setDocument(documentFound);
        return userRepository.save(userFound);
    }

    // TODO: deve criar tres colunas (email pendente, email status pedennte, e codigo), deve encaminhar uma mensagem no topico e fazer o envio de notificacao de email mostrando o codigo
    @Transactional
    @Override
    public UserEntity updateEmail(UUID userExternalId, String newEmail, UUID projectExternalId) {
        UserEntity userFound = getOneUser(userExternalId, projectExternalId);
        validateExistingUserWithEmail(newEmail, projectExternalId, userExternalId);

        userFound.setEmail(newEmail);
        return userRepository.save(userFound);
    }

    // TODO: deve criar tres colunas (email pendente, email status pedennte, e codigo), deve encaminhar uma mensagem no topico e fazer o envio de notificacao de email mostrando o codigo
    @Transactional
    @Override
    public UserEntity updatePassword(UUID userExternalId, String oldPassword, String newPassword, UUID projectExternalId) {
        UserEntity userFound = getOneUser(userExternalId, projectExternalId);
        if (!passwordEncoder.matches(oldPassword, userFound.getPassword())) {
            throw new BusinessRuleException("Old password does not match");
        }

        userFound.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(userFound);
    }

    // TODO: deve criar um outro endpoint que valida o codigo enviado por email e atualiza o email do usuario

    @Override
    public Page<UserEntity> getAllUsers(UUID projectExternalId, UserFilter userFilter, Pageable pageable) {
        return userRepository.findAll(
                new UserSpec(projectExternalId, userFilter),
                pageable
        );
    }

    @Override
    public void delete(UUID userExternalId, UUID projectExternalId) {
        UserEntity userFound = getOneUser(userExternalId, projectExternalId);
        try {
            userRepository.delete(userFound);
            userRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException(String.format("User with ID %s cannot be removed as it is in use.", userExternalId));
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

    private void validateExistingUserWithEmail(String email, UUID projectExternalId, UUID userExternalId) {
        userRepository.findOneByEmailAndProjectExternalId(
                email,
                projectExternalId
        ).ifPresent(user -> {
            String templateErrorMessage = "Already exists a user with this email in the project";
            if (Objects.isNull(userExternalId)) {
                throw new BusinessRuleException(templateErrorMessage);
            }

            if (!Objects.equals(userExternalId, user.getUserExternalId())) {
                throw new BusinessRuleException(templateErrorMessage);
            }
        });
    }
}
