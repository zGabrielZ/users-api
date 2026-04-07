package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.input.user.CreateUserInputDTO;
import lombok.SneakyThrows;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void setUp() {
        userIdExisting = UUID.fromString("e83a7a62-d131-4644-b8c4-2d551d1e252c");
        userIdNonExisting = UUID.fromString("ad34216d-25b0-4e6c-9452-b886a3747c8c");
        projectIdExisting = UUID.fromString("baa79cdd-c3ec-4392-bf70-214d49a74218");
        projectIdNonExisting = UUID.fromString("1bc0212e-f9df-4a61-bc8c-6843d67f9bb7");
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

    // TODO: validar os campos de validação @NotBlank e etc

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