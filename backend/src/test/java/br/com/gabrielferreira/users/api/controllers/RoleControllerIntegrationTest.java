package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.input.role.CreateRoleInputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for RoleController")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleControllerIntegrationTest extends BaseControllerIntegrationTest {

    private static final String URL = "/v1/roles";

    private UUID roleIdExisting;

    private UUID roleIdNonExisting;

    private UUID projectIdExisting;

    private UUID projectIdNonExisting;

    @BeforeEach
    void setUp() {
        roleIdExisting =  UUID.fromString("8857427d-8bdb-4432-ada8-a5737b238595");
        roleIdNonExisting = UUID.fromString("a63f3bcb-4e58-456f-a12d-a8b96de6ff75");
        projectIdExisting = UUID.fromString("baa79cdd-c3ec-4392-bf70-214d49a74218");
        projectIdNonExisting = UUID.fromString("1bc0212e-f9df-4a61-bc8c-6843d67f9bb7");
    }

    @Test
    @Order(1)
    @SneakyThrows
    void givenPayloadRoleWhenCreateThenReturnCreatedRole() {
        var createRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Role for client")
                .authority("ROLE_CLIENT")
                .build();
        String payload = objectMapper.writeValueAsString(createRoleInputDTO);

        ProjectEntity expectedProjectFound = projectRepository.findOneByProjectExternalId(projectIdExisting)
                .orElseThrow();

        mockMvc.perform(post(URL)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(createRoleInputDTO.description()))
                .andExpect(jsonPath("$.authority").value(createRoleInputDTO.authority()))
                .andExpect(jsonPath("$.roleExternalId").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.project.projectExternalId").value(expectedProjectFound.getProjectExternalId().toString()))
                .andExpect(jsonPath("$.project.name").value(expectedProjectFound.getName()))
                .andExpect(jsonPath("$.project.createdAt").value(expectedProjectFound.getCreatedAt().toString()));
    }

    @Test
    @Order(2)
    @SneakyThrows
    void givenPayloadRoleWhenCreateThenNotSaveDueToProjectNotFound() {
        var createRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Role for client")
                .authority("ROLE_CLIENT")
                .build();
        String payload = objectMapper.writeValueAsString(createRoleInputDTO);

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
    void givenPayloadRoleWhenCreateThenNotSaveDueToExistingAuthority() {
        var createRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Role for client")
                .authority("ROLE_ADMIN")
                .build();
        String payload = objectMapper.writeValueAsString(createRoleInputDTO);

        ProjectEntity expectedProjectFound = projectRepository.findOneByProjectExternalId(projectIdExisting)
                .orElseThrow();

        mockMvc.perform(post(URL)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value(String.format("A role with the authority '%s' already exists in the project '%s'.", createRoleInputDTO.authority(), expectedProjectFound.getName())))
                .andExpect(jsonPath("$.message").value("A business rule has been violated."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // TODO: fazer os testes de validação de campo para o post de roles
}