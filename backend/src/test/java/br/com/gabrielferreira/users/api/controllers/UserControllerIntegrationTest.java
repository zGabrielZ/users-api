package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.input.user.CreateUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdateDocumentUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdateEmailUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdateUserInputDTO;
import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for UserController")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIntegrationTest extends BaseControllerIntegrationTest {

    private static final String URL = "/v1/users";

    private UUID userIdExisting;

    private UUID userIdNonExisting;

    private UUID projectIdExisting;

    private UUID projectIdNonExisting;

    private UUID userIdExistingWithoutDocument;

    @BeforeEach
    void setUp() {
        userIdExisting = UUID.fromString("e83a7a62-d131-4644-b8c4-2d551d1e252c");
        userIdNonExisting = UUID.fromString("ad34216d-25b0-4e6c-9452-b886a3747c8c");
        projectIdExisting = UUID.fromString("baa79cdd-c3ec-4392-bf70-214d49a74218");
        projectIdNonExisting = UUID.fromString("1bc0212e-f9df-4a61-bc8c-6843d67f9bb7");
        userIdExistingWithoutDocument = UUID.fromString("d3629698-20fb-45eb-89f5-361bc449bc7d");
    }

    @Test
    @Order(1)
    @SneakyThrows
    void givenPayloadUserWhenCreateThenReturnCreatedUser() {
        var createUserInputDTO = CreateUserInputDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .password("Ac1@")
                .build();
        String payload = objectMapper.writeValueAsString(createUserInputDTO);

        mockMvc.perform(post(URL)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userExternalId").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(createUserInputDTO.firstName()))
                .andExpect(jsonPath("$.lastName").value(createUserInputDTO.lastName()))
                .andExpect(jsonPath("$.email").value(createUserInputDTO.email()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.document.documentExternalId").isNotEmpty())
                .andExpect(jsonPath("$.document.type").value("NONE"))
                .andExpect(jsonPath("$.document.number").doesNotExist());
    }

    @Test
    @Order(2)
    @SneakyThrows
    void givenPayloadUserWhenCreateThenNotSaveDueToProjectNotFound() {
        var createUserInputDTO = CreateUserInputDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .password("Ac1@")
                .build();
        String payload = objectMapper.writeValueAsString(createUserInputDTO);

        mockMvc.perform(post(URL)
                        .content(payload)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Project with ID %s not found.", projectIdNonExisting.toString())))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(3)
    @SneakyThrows
    void givenPayloadUserWhenCreateThenNotSaveDueToAlreadyExistsEmailInTheProject() {
        var createUserInputDTO = CreateUserInputDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("gabriel@email.com")
                .password("Ac1@")
                .build();
        String payload = objectMapper.writeValueAsString(createUserInputDTO);

        mockMvc.perform(post(URL)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value("Already exists a user with this email in the project"))
                .andExpect(jsonPath("$.message").value("A business rule has been violated."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidEmails")
    @Order(4)
    @SneakyThrows
    void givenPayloadUserWhenCreateThenNotSaveDueToEmailInvalid(String email) {
        var createUserInputDTO = CreateUserInputDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .password("Ac1@")
                .build();
        String payload = objectMapper.writeValueAsString(createUserInputDTO);

        mockMvc.perform(post(URL)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Data"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.message").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0].field").value("email"))
                .andExpect(jsonPath("$.fields[0].message").value("Email of the user must be a valid email address"));
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    @Order(5)
    @SneakyThrows
    void givenPayloadUserWhenCreateThenNotSaveDueToPasswordWeak(String password, String expectedErrorMessage) {
        var createUserInputDTO = CreateUserInputDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("teste@email.com")
                .password(password)
                .build();
        String payload = objectMapper.writeValueAsString(createUserInputDTO);

        mockMvc.perform(post(URL)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Data"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.message").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0].field").value("password"))
                .andExpect(jsonPath("$.fields[0].message").value(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserInputs")
    @Order(6)
    @SneakyThrows
    void givenInvalidUserPayloadWhenCreateThenReturnValidationError(CreateUserInputDTO createUserInputDTO, String expectedErrorMessage, String field) {
        String payload = objectMapper.writeValueAsString(createUserInputDTO);

        mockMvc.perform(post(URL)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Data"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.message").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0].field").value(field))
                .andExpect(jsonPath("$.fields[0].message").value(expectedErrorMessage));
    }

    @Test
    @Order(6)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenRetrieveByExternalIdThenReturnUser() {
        mockMvc.perform(get(URL.concat("/{userExternalId}"), userIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExternalId").value(userIdExisting.toString()))
                .andExpect(jsonPath("$.firstName").value("Gabriel"))
                .andExpect(jsonPath("$.lastName").value("Ferreira"))
                .andExpect(jsonPath("$.email").value("gabriel@email.com"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.document.documentExternalId").isNotEmpty())
                .andExpect(jsonPath("$.document.type").value("CPF"))
                .andExpect(jsonPath("$.document.number").value(Mask.formatDocument(DocumentType.CPF, "96742354771")));
    }

    @Test
    @Order(7)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdNonExistingWhenRetrieveByExternalIdThenReturnNotFound() {
        mockMvc.perform(get(URL.concat("/{userExternalId}"), userIdExisting)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(8)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdExistingWhenRetrieveByExternalIdThenReturnNotFound() {
        mockMvc.perform(get(URL.concat("/{userExternalId}"), userIdNonExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(9)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdNonExistingWhenRetrieveByExternalIdThenReturnNotFound() {
        mockMvc.perform(get(URL.concat("/{userExternalId}"), userIdNonExisting)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(10)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateThenReturnUpdatedUser() {
        var updateUserInputDTO = UpdateUserInputDTO.builder()
                .firstName("Gabriel Updated")
                .lastName("Ferreira Updated")
                .build();
        String payload = objectMapper.writeValueAsString(updateUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}"), userIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExternalId").value(userIdExisting.toString()))
                .andExpect(jsonPath("$.firstName").value(updateUserInputDTO.firstName()))
                .andExpect(jsonPath("$.lastName").value(updateUserInputDTO.lastName()))
                .andExpect(jsonPath("$.email").value("gabriel@email.com"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.document.documentExternalId").isNotEmpty())
                .andExpect(jsonPath("$.document.type").value("CPF"))
                .andExpect(jsonPath("$.document.number").value(Mask.formatDocument(DocumentType.CPF, "96742354771")));
    }

    @Test
    @Order(11)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdNonExistingWhenUpdateThenReturnNotFound() {
        var updateUserInputDTO = UpdateUserInputDTO.builder()
                .firstName("Gabriel Updated")
                .lastName("Ferreira Updated")
                .build();
        String payload = objectMapper.writeValueAsString(updateUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}"), userIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(12)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdExistingWhenUpdateThenReturnNotFound() {
        var updateUserInputDTO = UpdateUserInputDTO.builder()
                .firstName("Gabriel Updated")
                .lastName("Ferreira Updated")
                .build();
        String payload = objectMapper.writeValueAsString(updateUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}"), userIdNonExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(12)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdNonExistingWhenUpdateThenReturnNotFound() {
        var updateUserInputDTO = UpdateUserInputDTO.builder()
                .firstName("Gabriel Updated")
                .lastName("Ferreira Updated")
                .build();
        String payload = objectMapper.writeValueAsString(updateUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}"), userIdNonExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUserInputsUpdate")
    @Order(13)
    @SneakyThrows
    void givenInvalidUserPayloadWhenUpdateThenReturnValidationError(UpdateUserInputDTO updateUserInputDTO, String expectedErrorMessage, String field) {
        String payload = objectMapper.writeValueAsString(updateUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}"), userIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Data"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.message").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0].field").value(field))
                .andExpect(jsonPath("$.fields[0].message").value(expectedErrorMessage));
    }

    @Test
    @Order(14)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateDocumentThenReturnUpdatedUser() {
        var updateDocumentUserInputDTO = UpdateDocumentUserInputDTO.builder()
                .number("76497750037")
                .build();
        String payload = objectMapper.writeValueAsString(updateDocumentUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/document"), userIdExistingWithoutDocument)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExternalId").value(userIdExistingWithoutDocument.toString()))
                .andExpect(jsonPath("$.firstName").value("José"))
                .andExpect(jsonPath("$.lastName").value("Pereira da Silva"))
                .andExpect(jsonPath("$.email").value("jose@email.com"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.document.documentExternalId").isNotEmpty())
                .andExpect(jsonPath("$.document.type").value("CPF"))
                .andExpect(jsonPath("$.document.number").value(Mask.formatDocument(DocumentType.CPF, "76497750037")));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDocument")
    @Order(15)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateInvalidDocumentThenValidationError(String document, String expectedErrorMessage) {
        var updateDocumentUserInputDTO = UpdateDocumentUserInputDTO.builder()
                .number(document)
                .build();
        String payload = objectMapper.writeValueAsString(updateDocumentUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/document"), userIdExistingWithoutDocument)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Data"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.message").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0].field").value("number"))
                .andExpect(jsonPath("$.fields[0].message").value(expectedErrorMessage));
    }

    @Test
    @Order(16)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdNonExistingWhenUpdateDocumentThenReturnNotFound() {
        var updateDocumentUserInputDTO = UpdateDocumentUserInputDTO.builder()
                .number("76497750037")
                .build();
        String payload = objectMapper.writeValueAsString(updateDocumentUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/document"), userIdExistingWithoutDocument)
                        .content(payload)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdExistingWithoutDocument, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(17)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdExistingWhenUpdateDocumentThenReturnNotFound() {
        var updateDocumentUserInputDTO = UpdateDocumentUserInputDTO.builder()
                .number("76497750037")
                .build();
        String payload = objectMapper.writeValueAsString(updateDocumentUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/document"), userIdNonExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(18)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdNonExistingWhenUpdateDocumentThenReturnNotFound() {
        var updateDocumentUserInputDTO = UpdateDocumentUserInputDTO.builder()
                .number("76497750037")
                .build();
        String payload = objectMapper.writeValueAsString(updateDocumentUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/document"), userIdNonExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(19)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateDocumentThenReturnUserWithRegisteredDocument() {
        var updateDocumentUserInputDTO = UpdateDocumentUserInputDTO.builder()
                .number("76497750037")
                .build();
        String payload = objectMapper.writeValueAsString(updateDocumentUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/document"), userIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value("User already has a document registered"))
                .andExpect(jsonPath("$.message").value("A business rule has been violated."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(20)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateDocumentThenReturnAlreadyExistsUserWithDocument() {
        var updateDocumentUserInputDTO = UpdateDocumentUserInputDTO.builder()
                .number("96742354771")
                .build();
        String payload = objectMapper.writeValueAsString(updateDocumentUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/document"), userIdExistingWithoutDocument)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value("Already exists a user with this document in the project"))
                .andExpect(jsonPath("$.message").value("A business rule has been violated."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(21)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateEmailThenReturnUpdatedUser() {
        var updateEmailUserInputDTO = UpdateEmailUserInputDTO.builder()
                .email("ferreira@email.com")
                .build();
        String payload = objectMapper.writeValueAsString(updateEmailUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/email"), userIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userExternalId").value(userIdExisting.toString()))
                .andExpect(jsonPath("$.firstName").value("Gabriel"))
                .andExpect(jsonPath("$.lastName").value("Ferreira"))
                .andExpect(jsonPath("$.email").value(updateEmailUserInputDTO.email()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.document.documentExternalId").isNotEmpty())
                .andExpect(jsonPath("$.document.type").value("CPF"))
                .andExpect(jsonPath("$.document.number").value(Mask.formatDocument(DocumentType.CPF, "96742354771")));
    }

    @Test
    @Order(22)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateEmailThenReturnValidationError() {
        var updateEmailUserInputDTO = UpdateEmailUserInputDTO.builder()
                .email("jose@email.com")
                .build();
        String payload = objectMapper.writeValueAsString(updateEmailUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/email"), userIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value("Already exists a user with this email in the project"))
                .andExpect(jsonPath("$.message").value("A business rule has been violated."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidEmails")
    @Order(23)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateEmailThenReturnAlreadyExistsEmail(String invalidEmail) {
        var updateEmailUserInputDTO = UpdateEmailUserInputDTO.builder()
                .email(invalidEmail)
                .build();
        String payload = objectMapper.writeValueAsString(updateEmailUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/email"), userIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Data"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.message").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0].field").value("email"))
                .andExpect(jsonPath("$.fields[0].message").value("Email of the user must be a valid email address"));
    }

    @Test
    @Order(24)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenUpdateEmailThenNotSaveDueToEmailBlank() {
        var updateEmailUserInputDTO = UpdateEmailUserInputDTO.builder()
                .build();
        String payload = objectMapper.writeValueAsString(updateEmailUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/email"), userIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid Data"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.message").value("One or more fields are invalid. Please correct them and try again."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.fields").isArray())
                .andExpect(jsonPath("$.fields[0].field").value("email"))
                .andExpect(jsonPath("$.fields[0].message").value("Email of the user is required"));
    }

    @Test
    @Order(25)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdNonExistingWhenUpdateEmailThenReturnNotFound() {
        var updateEmailUserInputDTO = UpdateEmailUserInputDTO.builder()
                .email("email@email.com")
                .build();
        String payload = objectMapper.writeValueAsString(updateEmailUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/email"), userIdExistingWithoutDocument)
                        .content(payload)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdExistingWithoutDocument, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(26)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdExistingWhenUpdateEmailThenReturnNotFound() {
        var updateEmailUserInputDTO = UpdateEmailUserInputDTO.builder()
                .email("email@email.com")
                .build();
        String payload = objectMapper.writeValueAsString(updateEmailUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/email"), userIdNonExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(27)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdNonExistingWhenUpdateEmailThenReturnNotFound() {
        var updateEmailUserInputDTO = UpdateEmailUserInputDTO.builder()
                .email("email@email.com")
                .build();
        String payload = objectMapper.writeValueAsString(updateEmailUserInputDTO);

        mockMvc.perform(put(URL.concat("/{userExternalId}/email"), userIdNonExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // TODO: FAZER OS TESTES DO UPDATE PASSWORD, BUSCAR USUARIOS, DELETAR USUARIOS

    public static Stream<Arguments> provideInvalidDocument() {
        String expectedErrorMessageInvalidDocument = "Invalid CPF number.";
        String expectedErrorMessageDocumentRequired = "Document of the user is required";
        return Stream.of(
                Arguments.of("123.456.789-00", expectedErrorMessageInvalidDocument),
                Arguments.of("111.222.333-44", expectedErrorMessageInvalidDocument),
                Arguments.of("987.654.321-10", expectedErrorMessageInvalidDocument),
                Arguments.of("555.666.777-88", expectedErrorMessageInvalidDocument),
                Arguments.of("246.810.121-31", expectedErrorMessageInvalidDocument),
                Arguments.of(null, expectedErrorMessageDocumentRequired),
                Arguments.of("", expectedErrorMessageDocumentRequired)
        );
    }

    public static Stream<Arguments> provideInvalidUserInputsUpdate() {
        var userWithoutFirstName = UpdateUserInputDTO.builder()
                .firstName(null)
                .lastName("Doe")
                .build();

        var userExceededLimitFirstName = UpdateUserInputDTO.builder()
                .firstName(RandomStringUtils.secure().nextAlphabetic(256))
                .lastName("Doe")
                .build();

        var userWithoutLasNameName = UpdateUserInputDTO.builder()
                .firstName("Joe")
                .lastName(null)
                .build();

        var userExceededLimitLastName= UpdateUserInputDTO.builder()
                .firstName("Joe")
                .lastName(RandomStringUtils.secure().nextAlphabetic(256))
                .build();

        return Stream.of(
                Arguments.of(userWithoutFirstName, "First name of the user is required", "firstName"),
                Arguments.of(userExceededLimitFirstName, "First name of the user must be between 1 and 255 characters long", "firstName"),
                Arguments.of(userWithoutLasNameName, "Last name of the user is required", "lastName"),
                Arguments.of(userExceededLimitLastName, "Last name of the user must be between 1 and 255 characters long", "lastName")
        );
    }

    public static Stream<Arguments> provideInvalidUserInputs() {
        var userWithoutFirstName = CreateUserInputDTO.builder()
                .firstName(null)
                .lastName("Doe")
                .email("john@email.com")
                .password("Ac1@")
                .build();

        var userExceededLimitFirstName = CreateUserInputDTO.builder()
                .firstName(RandomStringUtils.secure().nextAlphabetic(256))
                .lastName("Doe")
                .email("john@email.com")
                .password("Ac1@")
                .build();

        var userWithoutLasNameName = CreateUserInputDTO.builder()
                .firstName("Joe")
                .lastName(null)
                .email("john@email.com")
                .password("Ac1@")
                .build();

        var userExceededLimitLastName= CreateUserInputDTO.builder()
                .firstName("Joe")
                .lastName(RandomStringUtils.secure().nextAlphabetic(256))
                .email("john@email.com")
                .password("Ac1@")
                .build();

        var userWithoutEmail = CreateUserInputDTO.builder()
                .firstName("Joe")
                .lastName("Doe")
                .email(null)
                .password("Ac1@")
                .build();

        var userWithoutPassword = CreateUserInputDTO.builder()
                .firstName("Joe")
                .lastName("Doe")
                .email("john@email.com")
                .password(null)
                .build();

        var userExceededLimitPassword = CreateUserInputDTO.builder()
                .firstName("Joe")
                .lastName("Doe")
                .email("john@email.com")
                .password("Ac1@".concat(RandomStringUtils.secure().nextAlphabetic(256)))
                .build();

        return Stream.of(
                Arguments.of(userWithoutFirstName, "First name of the user is required", "firstName"),
                Arguments.of(userExceededLimitFirstName, "First name of the user must be between 1 and 255 characters long", "firstName"),
                Arguments.of(userWithoutLasNameName, "Last name of the user is required", "lastName"),
                Arguments.of(userExceededLimitLastName, "Last name of the user must be between 1 and 255 characters long", "lastName"),
                Arguments.of(userWithoutEmail, "Email of the user is required", "email"),
                Arguments.of(userWithoutPassword, "Password of the user is required", "password"),
                Arguments.of(userExceededLimitPassword, "Password of the user must be between 1 and 255 characters long", "password")
        );
    }

    public static Stream<Arguments> invalidPasswords() {
        return Stream.of(
                Arguments.of("Ac1", "Password must contain at least one special character."),
                Arguments.of("ac1@", "Password must contain at least one uppercase letter."),
                Arguments.of("AC1@", "Password must contain at least one lowercase letter."),
                Arguments.of("Ac@", "Password must contain at least one digit.")
        );
    }

    public static Stream<Arguments> invalidEmails() {
        return Stream.of(
                Arguments.of("teste"),
                Arguments.of("teste@"),
                Arguments.of("@teste.com"),
                Arguments.of("teste.com"),
                Arguments.of("teste@.com"),
                Arguments.of("teste@com"),

                Arguments.of("teste@@email.com"),
                Arguments.of("te@ste@email.com"),

                Arguments.of("teste email@email.com"),
                Arguments.of("teste@email .com"),
                Arguments.of("teste@ email.com"),

                Arguments.of("teste<>@email.com"),
                Arguments.of("teste()@email.com"),
                Arguments.of("teste[]@email.com"),
                Arguments.of("teste;@email.com"),

                Arguments.of("teste@email"),
                Arguments.of("teste@email.c"),
                Arguments.of("teste@email..com"),
                Arguments.of("teste@-email.com"),
                Arguments.of("teste@email-.com"),

                Arguments.of(".teste@email.com"),
                Arguments.of("teste.@email.com"),
                Arguments.of("teste..email@email.com")
        );
    }
}