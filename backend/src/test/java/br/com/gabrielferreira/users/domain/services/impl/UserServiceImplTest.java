package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.UserNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.UserRepository;
import br.com.gabrielferreira.users.domain.repositories.filter.document.DocumentFilter;
import br.com.gabrielferreira.users.domain.repositories.filter.user.UserFilter;
import br.com.gabrielferreira.users.domain.repositories.projection.user.SummaryUserProjection;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import br.com.gabrielferreira.users.stub.document.DocumentEntityStub;
import br.com.gabrielferreira.users.stub.project.ProjectEntityStub;
import br.com.gabrielferreira.users.stub.user.UserEntityStub;
import br.com.gabrielferreira.users.utils.GenerateCPFUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Unit tests for UserServiceImpl")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UUID projectExternalId;

    private ProjectEntity projectEntity;

    private String encodedPassword;

    private String password;

    private UUID userExternalId;

    private OffsetDateTime offsetDateTime;

    @BeforeEach
    void setUp() {
        encodedPassword = "encodedPassword";
        password = "password";
        projectExternalId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        userExternalId = UUID.fromString("b01baffc-f4fe-4ff8-91d4-d4eeb1c7d944");
        projectEntity = ProjectEntityStub.createProjectEntity("Project A", projectExternalId);
        offsetDateTime = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    }

    @Test
    @Order(1)
    void givenUserEntityWithProjectExternalIdWhenSaveThenReturnUserEntityCreated() {
        UserEntity userEntity = UserEntityStub.createUserEntity(projectEntity);

        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        when(userRepository.findOneByEmailAndProjectExternalId(userEntity.getEmail(), projectExternalId))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(userEntity.getPassword()))
                .thenReturn(encodedPassword);

        UserEntity userEntityCreated = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, userEntity.getDocument());
        when(userRepository.save(userEntity))
                .thenReturn(userEntityCreated);

        UserEntity userEntitySaved = userService.save(userEntity, projectExternalId);

        assertNotNull(userEntitySaved);
        assertEquals(userEntityCreated, userEntitySaved);
        verify(projectService).getOneProject(projectExternalId);
        verify(userRepository).findOneByEmailAndProjectExternalId(userEntity.getEmail(), projectExternalId);
        verify(userRepository).save(userEntity);
        verify(passwordEncoder).encode(password);
    }

    @Test
    @Order(2)
    void givenUserEntityWithoutDocumentWithProjectExternalIdWhenSaveThenReturnUserEntityCreated() {
        UserEntity userEntity = UserEntityStub.createUserEntity(projectEntity);
        userEntity.setDocument(null);

        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        when(userRepository.findOneByEmailAndProjectExternalId(userEntity.getEmail(), projectExternalId))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(userEntity.getPassword()))
                .thenReturn(encodedPassword);

        DocumentEntity expectedDocumentEntity = DocumentEntityStub.documentNoneEntityCreated();
        UserEntity userEntityCreated = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, expectedDocumentEntity);
        when(userRepository.save(userEntity))
                .thenReturn(userEntityCreated);

        UserEntity userEntitySaved = userService.save(userEntity, projectExternalId);

        assertNotNull(userEntitySaved);
        assertEquals(userEntityCreated, userEntitySaved);
        verify(projectService).getOneProject(projectExternalId);
        verify(userRepository).findOneByEmailAndProjectExternalId(userEntity.getEmail(), projectExternalId);
        verify(userRepository).save(userEntity);
        verify(passwordEncoder).encode(password);
    }

    @Test
    @Order(3)
    void givenUserEntityWithProjectExternalIdWhenSaveThenNotSaveDueToExistingEmail() {
        UserEntity userEntity = UserEntityStub.createUserEntity(projectEntity);

        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        SummaryUserProjection summaryUserProjection = mock(SummaryUserProjection.class);
        when(userRepository.findOneByEmailAndProjectExternalId(userEntity.getEmail(), projectExternalId))
                .thenReturn(Optional.of(summaryUserProjection));

        BusinessRuleException businessRuleException = assertThrows(BusinessRuleException.class, () -> userService.save(userEntity, projectExternalId));

        String expectedMessage = "Already exists a user with this email in the project";
        assertEquals(expectedMessage, businessRuleException.getMessage());
        verify(projectService).getOneProject(projectExternalId);
        verify(userRepository).findOneByEmailAndProjectExternalId(userEntity.getEmail(), projectExternalId);
        verify(userRepository, never()).save(userEntity);
        verify(passwordEncoder, never()).encode(password);
    }

    @Test
    @Order(4)
    void givenUserExternalIdWithProjectExternalIdWhenGetOneUserThenReturnUserEntity() {
        DocumentEntity document = DocumentEntityStub.documentCpfEntityCreated();
        UserEntity userEntity = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, document);

        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntity));

        UserEntity userEntityFound = userService.getOneUser(userExternalId, projectExternalId);

        assertNotNull(userEntityFound);
        assertEquals(userEntity, userEntityFound);
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
    }

    @Test
    @Order(5)
    void givenUserExternalIdWithProjectExternalIdWhenGetOneUserThenThrowUserNotFoundException() {
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getOneUser(userExternalId, projectExternalId));

        String expectedMessage = String.format("User with ID %s not found for Project with ID %s", userExternalId, projectExternalId);
        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
    }

    @Test
    @Order(6)
    void givenUserEntityWithUserExternalIdAndProjectExternalIdWhenUpdateUserThenReturnUserEntityUpdated() {
        UserEntity userEntity = UserEntity.builder()
                .firstName("John Doe")
                .lastName("Smith")
                .build();

        DocumentEntity document = DocumentEntityStub.documentCpfEntityCreated();
        UserEntity userEntityFound = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, document);
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntityFound));

        userEntityFound.setFirstName(userEntity.getFirstName());
        userEntityFound.setLastName(userEntity.getLastName());
        when(userRepository.save(userEntityFound))
                .thenReturn(userEntityFound);

        UserEntity userEntityResult = userService.update(userExternalId, userEntity, projectExternalId);

        assertNotNull(userEntityResult);
        assertEquals(userEntityFound, userEntityResult);
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
        verify(userRepository).save(userEntityFound);
    }

    @Test
    @Order(7)
    void givenUserExternalIdWithDocumentEntityAndProjectExternalIdWhenUpdateDocumentThenReturnDocumentEntityUpdated() {
        DocumentEntity document = DocumentEntityStub.documentCpfEntityCreated();

        DocumentEntity expectedDocumentEntity = DocumentEntityStub.documentNoneEntityCreated();
        UserEntity userEntityFound = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, expectedDocumentEntity);
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntityFound));

        when(userRepository.findOneUserByDocumentAndProjectExternalId(
                document.getType(),
                document.getNumber(),
                projectExternalId
        )).thenReturn(Optional.empty());

        UserEntity userEntityUpdated = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, document);
        when(userRepository.save(userEntityFound))
                .thenReturn(userEntityUpdated);

        UserEntity userEntityResult = userService.updateDocument(userExternalId, document, projectExternalId);

        assertNotNull(userEntityResult);
        assertEquals(userEntityUpdated, userEntityResult);
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
        verify(userRepository).findOneUserByDocumentAndProjectExternalId(document.getType(), document.getNumber(), projectExternalId);
        verify(userRepository).save(userEntityFound);
    }

    @Test
    @Order(8)
    void givenUserExternalIdWithDocumentEntityAndProjectExternalIdWhenUpdateDocumentThenNotSaveDueToExistingNumber() {
        DocumentEntity document = DocumentEntityStub.documentCpfEntityCreated();

        UserEntity userEntityFound = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, document);
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntityFound));

        BusinessRuleException businessRuleException = assertThrows(BusinessRuleException.class, () -> userService.updateDocument(userExternalId, document, projectExternalId));

        String expectedMessage = "User already has a document registered";
        assertEquals(expectedMessage, businessRuleException.getMessage());
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
        verify(userRepository, never()).findOneUserByDocumentAndProjectExternalId(document.getType(), document.getNumber(), projectExternalId);
        verify(userRepository, never()).save(userEntityFound);
    }

    @Test
    @Order(9)
    void givenUserExternalIdWithDocumentEntityAndProjectExternalIdWhenUpdateDocumentThenNotSaveDueToExistingDocumentInProject() {
        DocumentEntity document = DocumentEntityStub.documentCpfEntityCreated();

        DocumentEntity expectedDocumentEntity = DocumentEntityStub.documentNoneEntityCreated();
        UserEntity userEntityFound = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, expectedDocumentEntity);
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntityFound));

        SummaryUserProjection summaryUserProjection = mock(SummaryUserProjection.class);
        when(userRepository.findOneUserByDocumentAndProjectExternalId(document.getType(), document.getNumber(), projectExternalId))
                .thenReturn(Optional.of(summaryUserProjection));

        BusinessRuleException businessRuleException = assertThrows(BusinessRuleException.class, () -> userService.updateDocument(userExternalId, document, projectExternalId));

        String expectedMessage = "Already exists a user with this document in the project";
        assertEquals(expectedMessage, businessRuleException.getMessage());
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
        verify(userRepository).findOneUserByDocumentAndProjectExternalId(document.getType(), document.getNumber(), projectExternalId);
        verify(userRepository, never()).save(userEntityFound);
    }

    @Test
    @Order(10)
    void givenUserExternalIdWithNewEmailAndProjectExternalIdWhenUpdateEmailThenUserEmailUpdated() {
        String newEmail = "user@email.com";

        UserEntity userEntityFound = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, DocumentEntityStub.documentCpfEntityCreated());
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntityFound));

        when(userRepository.findOneByEmailAndProjectExternalId(newEmail, projectExternalId))
                .thenReturn(Optional.empty());

        userEntityFound.setEmail(newEmail);
        when(userRepository.save(userEntityFound))
                .thenReturn(userEntityFound);

        UserEntity userEntityResult = userService.updateEmail(userExternalId, newEmail, projectExternalId);

        assertNotNull(userEntityResult);
        assertEquals(userEntityFound, userEntityResult);
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
        verify(userRepository).findOneByEmailAndProjectExternalId(newEmail, projectExternalId);
        verify(userRepository).save(userEntityFound);
    }

    @Test
    @Order(11)
    void givenUserExternalIdWithNewEmailAndProjectExternalIdWhenUpdateEmailThenNotUpdateDueToEmailAlreadyExists() {
        String newEmail = "user@email.com";

        UserEntity userEntityFound = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, DocumentEntityStub.documentCpfEntityCreated());
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntityFound));

        SummaryUserProjection summaryUserProjection = mock(SummaryUserProjection.class);
        when(userRepository.findOneByEmailAndProjectExternalId(newEmail, projectExternalId))
                .thenReturn(Optional.of(summaryUserProjection));

        BusinessRuleException businessRuleException = assertThrows(BusinessRuleException.class, () -> userService.updateEmail(userExternalId, newEmail, projectExternalId));

        String expectedMessage = "Already exists a user with this email in the project";
        assertEquals(expectedMessage, businessRuleException.getMessage());
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
        verify(userRepository).findOneByEmailAndProjectExternalId(newEmail, projectExternalId);
        verify(userRepository, never()).save(userEntityFound);
    }

    @Test
    @Order(12)
    void givenUserExternalIdWithPasswordAndProjectExternalIdWhenUpdatePasswordThenUserPasswordUpdated() {
         String newPassword = "newPassword";

        UserEntity userEntityFound = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, DocumentEntityStub.documentCpfEntityCreated());
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntityFound));

        when(passwordEncoder.matches(password, userEntityFound.getPassword()))
                .thenReturn(true);

        when(passwordEncoder.encode(newPassword))
                .thenReturn(encodedPassword);

        userEntityFound.setPassword(encodedPassword);
        when(userRepository.save(userEntityFound))
                .thenReturn(userEntityFound);

        UserEntity userEntityResult = userService.updatePassword(userExternalId, password, newPassword, projectExternalId);

        assertNotNull(userEntityResult);
        assertEquals(userEntityFound, userEntityResult);
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
        verify(passwordEncoder).matches(password, userEntityFound.getPassword());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(userEntityFound);
    }

    @Test
    @Order(13)
    void givenUserExternalIdWithPasswordAndProjectExternalIdWhenUpdatePasswordThenNotUpdateDueToOldPasswordDoesNotMatch() {
        String newPassword = "newPassword";

        UserEntity userEntityFound = UserEntityStub.userEntityCreated(projectEntity, encodedPassword, DocumentEntityStub.documentCpfEntityCreated());
        when(userRepository.findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId))
                .thenReturn(Optional.of(userEntityFound));

        when(passwordEncoder.matches(password, userEntityFound.getPassword()))
                .thenReturn(false);

        BusinessRuleException businessRuleException = assertThrows(BusinessRuleException.class, () -> userService.updatePassword(userExternalId, password, newPassword, projectExternalId));

        String expectedMessage = "Old password does not match";
        assertEquals(expectedMessage, businessRuleException.getMessage());
        verify(userRepository).findOneByUserExternalIdAndProjectExternalId(userExternalId, projectExternalId);
        verify(passwordEncoder).matches(password, userEntityFound.getPassword());
        verify(passwordEncoder, never()).encode(newPassword);
        verify(userRepository, never()).save(userEntityFound);
    }

    @Test
    @Order(14)
    void givenUserFilterWithPageableWhenGetAllUsersThenReturnPageOfUserEntity() {
        var projectFilter = UserFilter.builder()
                .userExternalId(userExternalId)
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .document(
                        DocumentFilter.builder()
                                .type(DocumentType.CPF)
                                .number(GenerateCPFUtils.generateCPF())
                                .build()
                )
                .createdAtFrom(offsetDateTime)
                .createdAtTo(offsetDateTime)
                .build();

        var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
        Page<UserEntity> userEntityPage = UserEntityStub.createPageOfUserEntities(pageRequest);
        when(userRepository.findAll(ArgumentMatchers.<Specification<UserEntity>>any(), eq(pageRequest)))
                .thenReturn(userEntityPage);

        Page<UserEntity> resultPage = userService.getAllUsers(projectExternalId, projectFilter, pageRequest);
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        verify(userRepository).findAll(ArgumentMatchers.<Specification<UserEntity>>any(), eq(pageRequest));
    }
}