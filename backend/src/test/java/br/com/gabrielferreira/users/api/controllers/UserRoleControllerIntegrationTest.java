package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.filter.role.RoleFilterDTO;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.utils.GenerateCPFUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for UserRoleController")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRoleControllerIntegrationTest extends BaseControllerIntegrationTest {

    private static final String URL = "/v1/users/%s/roles";

    private UUID userIdExisting;

    private UUID userIdNonExisting;

    private UUID projectIdExisting;

    private UUID projectIdNonExisting;

    private UUID roleIdExisting;

    private UUID roleIdNonExisting;

    private UUID roleIdExistingAlreadyAssociated;

    @BeforeEach
    void setUp() {
        userIdExisting = UUID.fromString("e83a7a62-d131-4644-b8c4-2d551d1e252c");
        userIdNonExisting = UUID.fromString("ad34216d-25b0-4e6c-9452-b886a3747c8c");
        projectIdExisting = UUID.fromString("baa79cdd-c3ec-4392-bf70-214d49a74218");
        projectIdNonExisting = UUID.fromString("1bc0212e-f9df-4a61-bc8c-6843d67f9bb7");
        roleIdExisting = UUID.fromString("1a7cf105-8bae-4ffc-a13f-aec32f5cd0e8");
        roleIdNonExisting = UUID.fromString("2c0f5367-d102-489a-8171-a80d104da211");
        roleIdExistingAlreadyAssociated = UUID.fromString("34fa7cf1-6b71-4989-bb92-f48068dc8967");
    }

    @Test
    @Order(1)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenAssociateRoleThenReturnRoleAssociated() {
        mockMvc.perform(put(String.format(URL, userIdExisting) + "/" + roleIdExisting)
                .header("projectExternalId", projectIdExisting)
                .contentType(MEDIA_TYPE_JSON)
                .accept(MEDIA_TYPE_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdExistingWhenAssociateRoleThenReturnNotFound() {
        mockMvc.perform(put(String.format(URL, userIdNonExisting) + "/" + roleIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(3)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdNonExistingWhenAssociateRoleThenReturnNotFound() {
        mockMvc.perform(put(String.format(URL, userIdExisting) + "/" + roleIdExisting)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(4)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenAssociateRoleNonExistingThenReturnNotFound() {
        mockMvc.perform(put(String.format(URL, userIdExisting) + "/" + roleIdNonExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Role with ID %s not found for Project with ID %s", roleIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(5)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenAssociateRoleThenReturnRoleAlreadyAssociated() {
        mockMvc.perform(get(String.format(URL, userIdExisting))
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].roleExternalId").value(roleIdExistingAlreadyAssociated.toString()))
                .andExpect(jsonPath("$.content[0].description").value("Manager"))
                .andExpect(jsonPath("$.content[0].authority").value("ROLE_MANAGER"))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));

        mockMvc.perform(put(String.format(URL, userIdExisting) + "/" + roleIdExistingAlreadyAssociated)
                .header("projectExternalId", projectIdExisting)
                .contentType(MEDIA_TYPE_JSON)
                .accept(MEDIA_TYPE_JSON)
        ).andExpect(status().isOk());

        mockMvc.perform(get(String.format(URL, userIdExisting))
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].roleExternalId").value(roleIdExistingAlreadyAssociated.toString()))
                .andExpect(jsonPath("$.content[0].description").value("Manager"))
                .andExpect(jsonPath("$.content[0].authority").value("ROLE_MANAGER"))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @Order(6)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenDisassociateRoleThenReturnRoleDisassociated() {
        mockMvc.perform(delete(String.format(URL, userIdExisting) + "/" + roleIdExistingAlreadyAssociated)
                .header("projectExternalId", projectIdExisting)
                .contentType(MEDIA_TYPE_JSON)
                .accept(MEDIA_TYPE_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @Order(7)
    @SneakyThrows
    void givenUserExternalIdNonExistingWithProjectExternalIdExistingWhenDisassociateRoleThenReturnNotFound() {
        mockMvc.perform(delete(String.format(URL, userIdNonExisting) + "/" + roleIdExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(8)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdNonExistingWhenDisassociateRoleThenReturnNotFound() {
        mockMvc.perform(delete(String.format(URL, userIdExisting) + "/" + roleIdExisting)
                        .header("projectExternalId", projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("User with ID %s not found for Project with ID %s", userIdExisting, projectIdNonExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(9)
    @SneakyThrows
    void givenUserExternalIdExistingWithProjectExternalIdExistingWhenDisassociateRoleNonExistingThenReturnNotFound() {
        mockMvc.perform(put(String.format(URL, userIdExisting) + "/" + roleIdNonExisting)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Role with ID %s not found for Project with ID %s", roleIdNonExisting, projectIdExisting)))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(10)
    @SneakyThrows
    void givenRoleFilterDtoWhenListAllThenReturnRole() {
        UserEntity userEntity = createUser(
                DocumentEntity.builder()
                        .type(DocumentType.CPF)
                        .number(GenerateCPFUtils.generateCPF())
                        .build(),
                roleIdExisting,
                projectIdExisting
        );

        associateRole(
                userEntity.getUserExternalId(),
                projectIdExisting,
                roleIdExisting
        );

        RoleEntity roleEntity = userEntity.getRoles().get(0);

        var roleFilterDTO = RoleFilterDTO.builder()
                .roleExternalId(roleEntity.getRoleExternalId())
                .description(roleEntity.getDescription())
                .authority(roleEntity.getAuthority())
                .createdAtTo(roleEntity.getCreatedAt())
                .createdAtFrom(roleEntity.getCreatedAt())
                .build();

        String payload = objectMapper.writeValueAsString(roleFilterDTO);

        String url = String.format(URL, userEntity.getUserExternalId()) + "?sort=createdAt,DESC";
        mockMvc.perform(get(url)
                        .header("projectExternalId", projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                        .content(payload)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].roleExternalId").value(roleEntity.getRoleExternalId().toString()))
                .andExpect(jsonPath("$.content[0].description").value(roleEntity.getDescription()))
                .andExpect(jsonPath("$.content[0].authority").value(roleEntity.getAuthority()))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }
}