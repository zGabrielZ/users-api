package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.filter.role.RoleFilterDTO;
import br.com.gabrielferreira.users.api.dtos.input.role.CreateRoleInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.role.UpdateRoleInputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.stub.user.UserEntityStub;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
        roleIdExisting = UUID.fromString("8857427d-8bdb-4432-ada8-a5737b238595");
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
        OffsetDateTime expectedCreatedAtProject = expectedProjectFound.getCreatedAt();

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
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();

                    String jsonDate = JsonPath.read(json, "$.project.createdAt");
                    OffsetDateTime actual = OffsetDateTime.parse(jsonDate);

                    assertEquals(expectedCreatedAtProject, actual);
                });
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

    @Test
    @Order(4)
    @SneakyThrows
    void givenPayloadRoleWhenCreateThenNotSaveDueToAuthorityBlank() {
        var createRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Role for client")
                .build();
        String payload = objectMapper.writeValueAsString(createRoleInputDTO);

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
                .andExpect(jsonPath("$.fields[0].field").value("authority"))
                .andExpect(jsonPath("$.fields[0].message").value("Authority of the role is required"));
    }

    @Test
    @Order(5)
    @SneakyThrows
    void givenPayloadRoleWhenCreateThenNotSaveDueToDescriptionBlank() {
        var createRoleInputDTO = CreateRoleInputDTO.builder()
                .authority("ROLE_CLIENT")
                .build();
        String payload = objectMapper.writeValueAsString(createRoleInputDTO);

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
                .andExpect(jsonPath("$.fields[0].field").value("description"))
                .andExpect(jsonPath("$.fields[0].message").value("Description of the role is required"));
    }

    @Test
    @Order(6)
    @SneakyThrows
    void givenPayloadRoleWhenCreateThenThrowDueToDescriptionExceedingMaxLength() {
        var createRoleInputDTO = CreateRoleInputDTO.builder()
                .description(RandomStringUtils.secure().nextAlphabetic(256))
                .authority("ROLE_CLIENT")
                .build();
        String payload = objectMapper.writeValueAsString(createRoleInputDTO);

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
                .andExpect(jsonPath("$.fields[0].field").value("description"))
                .andExpect(jsonPath("$.fields[0].message").value("Description of the role must be between 1 and 255 characters long"));
    }

    @Test
    @Order(7)
    @SneakyThrows
    void givenPayloadRoleWhenCreateThenThrowDueToAuthorityExceedingMaxLength() {
        var createRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Role for client")
                .authority(RandomStringUtils.secure().nextAlphabetic(256))
                .build();
        String payload = objectMapper.writeValueAsString(createRoleInputDTO);

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
                .andExpect(jsonPath("$.fields[0].field").value("authority"))
                .andExpect(jsonPath("$.fields[0].message").value("Authority of the role must be between 1 and 255 characters long"));
    }

    @Test
    @Order(8)
    @SneakyThrows
    void givenRoleExternalIdExistingWithProjectExternalIdExistingWhenRetrieveByExternalIdThenReturnRole() {
        mockMvc.perform(get(URL.concat("/{roleExternalId}"), roleIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleExternalId").value(roleIdExisting.toString()))
                .andExpect(jsonPath("$.description").value("Administrator"))
                .andExpect(jsonPath("$.authority").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.project.projectExternalId").value(projectIdExisting.toString()))
                .andExpect(jsonPath("$.project.name").value("Project B"))
                .andExpect(jsonPath("$.project.createdAt").isNotEmpty());
    }

    @Test
    @Order(9)
    @SneakyThrows
    void givenRoleExternalIdExistingWithProjectExternalIdNonExistingWhenRetrieveByExternalIdThenReturnNotFound() {
        mockMvc.perform(get(URL.concat("/{roleExternalId}"), roleIdExisting)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Role with ID %s not found for Project with ID %s", roleIdExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(10)
    @SneakyThrows
    void givenRoleExternalIdNonExistingWithProjectExternalIdExistingWhenRetrieveByExternalIdThenReturnNotFound() {
        mockMvc.perform(get(URL.concat("/{roleExternalId}"), roleIdNonExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Role with ID %s not found for Project with ID %s", roleIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(11)
    @SneakyThrows
    void givenRoleExternalIdNonExistingWithProjectExternalIdNonExistingWhenRetrieveByExternalIdThenReturnNotFound() {
        mockMvc.perform(get(URL.concat("/{roleExternalId}"), roleIdNonExisting)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Role with ID %s not found for Project with ID %s", roleIdNonExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(12)
    @SneakyThrows
    void givenRoleExternalIdExistingWithProjectExternalIdExistingWhenUpdateThenReturnUpdatedRole() {
        var updateRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Administrator updated")
                .authority("ROLE_ADMIN_UPDATED")
                .build();
        String payload = objectMapper.writeValueAsString(updateRoleInputDTO);

        ProjectEntity expectedProjectFound = projectRepository.findOneByProjectExternalId(projectIdExisting)
                .orElseThrow();
        OffsetDateTime expectedCreatedAtProject = expectedProjectFound.getCreatedAt();

        mockMvc.perform(put(URL.concat("/{roleExternalId}"), roleIdExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleExternalId").value(roleIdExisting.toString()))
                .andExpect(jsonPath("$.description").value(updateRoleInputDTO.description()))
                .andExpect(jsonPath("$.authority").value(updateRoleInputDTO.authority()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.project.projectExternalId").value(expectedProjectFound.getProjectExternalId().toString()))
                .andExpect(jsonPath("$.project.name").value(expectedProjectFound.getName()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();

                    String jsonDate = JsonPath.read(json, "$.project.createdAt");
                    OffsetDateTime actual = OffsetDateTime.parse(jsonDate);

                    assertEquals(expectedCreatedAtProject, actual);
                });
    }

    @Test
    @Order(13)
    @SneakyThrows
    void givenRoleExternalIdExistingWithProjectExternalIdExistingWhenUpdateThenNotUpdateDueToExistingAuthority() {
        // Create another role with authority ROLE_USER to make the update fail due to existing authority in the same project
        ProjectEntity expectedProjectFound = projectRepository.findOneByProjectExternalId(projectIdExisting)
                .orElseThrow();
        RoleEntity roleEntity = RoleEntity.builder()
                .description("Test")
                .authority("ROLE_TEST")
                .project(expectedProjectFound)
                .build();
        roleEntity = roleRepository.saveAndFlush(roleEntity);

        var updateRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Administrator updated")
                .authority("ROLE_ADMIN")
                .build();
        String payload = objectMapper.writeValueAsString(updateRoleInputDTO);


        mockMvc.perform(put(URL.concat("/{roleExternalId}"), roleEntity.getRoleExternalId())
                        .header("projectExternalId", projectIdExisting)
                        .content(payload)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value(String.format("A role with the authority '%s' already exists in the project '%s'.", updateRoleInputDTO.authority(), expectedProjectFound.getName())))
                .andExpect(jsonPath("$.message").value("A business rule has been violated."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(14)
    @SneakyThrows
    void givenRoleExternalIdNonExistingWithProjectExternalIdExistingWhenUpdateThenReturnNotFound() {
        var updateRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Administrator updated")
                .authority("ROLE_ADMIN_UPDATED")
                .build();
        String payload = objectMapper.writeValueAsString(updateRoleInputDTO);

        mockMvc.perform(put(URL.concat("/{roleExternalId}"), roleIdNonExisting)
                        .content(payload)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Role with ID %s not found for Project with ID %s", roleIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(15)
    @SneakyThrows
    void givenPayloadRoleWithRoleExternalIdExistingWithProjectExternalIdExistingWhenUpdateThenNotSaveDueToAuthorityBlank() {
        var updateRoleInputDTO = UpdateRoleInputDTO.builder()
                .description("Role for client")
                .build();
        String payload = objectMapper.writeValueAsString(updateRoleInputDTO);

        mockMvc.perform(put(URL.concat("/{roleExternalId}"), roleIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .content(payload)
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
                .andExpect(jsonPath("$.fields[0].field").value("authority"))
                .andExpect(jsonPath("$.fields[0].message").value("Authority of the role is required"));
    }

    @Test
    @Order(16)
    @SneakyThrows
    void givenPayloadRoleWithRoleExternalIdExistingWithProjectExternalIdExistingWhenUpdateThenNotSaveDueToDescriptionBlank() {
        var updateRoleInputDTO = UpdateRoleInputDTO.builder()
                .authority("ROLE_CLIENT")
                .build();
        String payload = objectMapper.writeValueAsString(updateRoleInputDTO);

        mockMvc.perform(put(URL.concat("/{roleExternalId}"), roleIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .content(payload)
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
                .andExpect(jsonPath("$.fields[0].field").value("description"))
                .andExpect(jsonPath("$.fields[0].message").value("Description of the role is required"));
    }

    @Test
    @Order(17)
    @SneakyThrows
    void givenPayloadRoleWithRoleExternalIdExistingWithProjectExternalIdExistingWhenUpdateThenThrowDueToDescriptionExceedingMaxLength() {
        var updateRoleInputDTO = UpdateRoleInputDTO.builder()
                .description(RandomStringUtils.secure().nextAlphabetic(256))
                .authority("ROLE_CLIENT")
                .build();
        String payload = objectMapper.writeValueAsString(updateRoleInputDTO);

        mockMvc.perform(put(URL.concat("/{roleExternalId}"), roleIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .content(payload)
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
                .andExpect(jsonPath("$.fields[0].field").value("description"))
                .andExpect(jsonPath("$.fields[0].message").value("Description of the role must be between 1 and 255 characters long"));
    }

    @Test
    @Order(18)
    @SneakyThrows
    void givenPayloadRoleWithRoleExternalIdExistingWithProjectExternalIdWhenUpdateThenThrowDueToAuthorityExceedingMaxLength() {
        var updateRoleInputDTO = UpdateRoleInputDTO.builder()
                .description("Role for client")
                .authority(RandomStringUtils.secure().nextAlphabetic(256))
                .build();
        String payload = objectMapper.writeValueAsString(updateRoleInputDTO);

        mockMvc.perform(put(URL.concat("/{roleExternalId}"), roleIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .content(payload)
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
                .andExpect(jsonPath("$.fields[0].field").value("authority"))
                .andExpect(jsonPath("$.fields[0].message").value("Authority of the role must be between 1 and 255 characters long"));
    }

    @Test
    @Order(19)
    @SneakyThrows
    void givenRoleExternalIdWithProjectExternalIdExistingWhenDeleteThenReturnNoContent() {
        mockMvc.perform(delete(URL.concat("/{roleExternalId}"), roleIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(20)
    @SneakyThrows
    void givenRoleExternalIdNonExistingWithProjectExternalIdExistingWhenDeleteThenReturnNotFound() {
        mockMvc.perform(delete(URL.concat("/{roleExternalId}"), roleIdNonExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Role with ID %s not found for Project with ID %s", roleIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(21)
    @SneakyThrows
    void givenRoleExternalIdWithProjectExternalIdExistingWhenDeleteThenThrowDueToRoleInUse() {
        // Create another project and user to associate with the existing project to make it in use
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name("Project C")
                .build();
        projectEntity = projectRepository.saveAndFlush(projectEntity);

        // Create another role for the existing project to make it in use
        RoleEntity roleEntity = RoleEntity.builder()
                .description("Test")
                .authority("ROLE_TEST")
                .project(projectEntity)
                .build();
        roleEntity = roleRepository.saveAndFlush(roleEntity);

        // Associate the user with the existing project to make it in use
        UserEntity userEntity = UserEntityStub.createUserEntity(projectEntity);
        userEntity.setRoles(new ArrayList<>());
        userEntity.getRoles().add(roleEntity);
        userRepository.saveAndFlush(userEntity);

        mockMvc.perform(delete(URL.concat("/{roleExternalId}"), roleEntity.getRoleExternalId())
                        .header("projectExternalId", projectEntity.getProjectExternalId())
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Entity In Use"))
                .andExpect(jsonPath("$.detail").value(String.format("Role with ID %s cannot be removed as it is in use.", roleEntity.getRoleExternalId())))
                .andExpect(jsonPath("$.message").value("The entity is currently in use and cannot be modified or deleted."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(22)
    @SneakyThrows
    void givenRoleFilterDtoWhenListAllThenReturnRole() {
        // Create another project and user to associate with the existing project to make it in use
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name("Project C")
                .build();
        projectEntity = projectRepository.saveAndFlush(projectEntity);
        OffsetDateTime expectedCreatedAtProject = projectEntity.getCreatedAt();

        // Create another role for the existing project to make it in use
        RoleEntity roleEntity = RoleEntity.builder()
                .description("Test")
                .authority("ROLE_TEST")
                .project(projectEntity)
                .build();
        roleEntity = roleRepository.saveAndFlush(roleEntity);
        OffsetDateTime expectedCreatedAtRole = roleEntity.getCreatedAt();

        var roleFilterDTO = RoleFilterDTO.builder()
                .roleExternalId(roleEntity.getRoleExternalId())
                .description(roleEntity.getDescription())
                .authority(roleEntity.getAuthority())
                .createdAtTo(roleEntity.getCreatedAt())
                .createdAtFrom(roleEntity.getCreatedAt())
                .build();

        String payload = objectMapper.writeValueAsString(roleFilterDTO);

        String url = URL.concat("?sort=createdAt,DESC");
        mockMvc.perform(get(url)
                        .header("projectExternalId", projectEntity.getProjectExternalId())
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                        .content(payload)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].roleExternalId").value(roleEntity.getRoleExternalId().toString()))
                .andExpect(jsonPath("$.content[0].description").value(roleEntity.getDescription()))
                .andExpect(jsonPath("$.content[0].authority").value(roleEntity.getAuthority()))
                .andExpect(jsonPath("$.content[0].project.projectExternalId").value(projectEntity.getProjectExternalId().toString()))
                .andExpect(jsonPath("$.content[0].project.name").value(projectEntity.getName()))
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();

                    String jsonDate = JsonPath.read(json, "$.content[0].createdAt");
                    OffsetDateTime actual = OffsetDateTime.parse(jsonDate);

                    assertEquals(expectedCreatedAtRole, actual);
                })
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();

                    String jsonDate = JsonPath.read(json, "$.content[0].project.createdAt");
                    OffsetDateTime actual = OffsetDateTime.parse(jsonDate);

                    assertEquals(expectedCreatedAtProject, actual);
                })
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }
}