package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.filter.project.ProjectFilterDTO;
import br.com.gabrielferreira.users.api.dtos.input.project.CreateProjectInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.project.UpdateProjectInputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.repositories.ProjectRepository;
import br.com.gabrielferreira.users.domain.repositories.UserRepository;
import br.com.gabrielferreira.users.stub.user.UserEntityStub;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Integration tests for ProjectController")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectControllerIntegrationTest {

    private static final String URL = "/v1/projects";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.APPLICATION_JSON;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected UserRepository userRepository;

    private UUID projectIdExisting;

    private UUID projectIdNonExisting;

    @BeforeEach
    void setUp() {
        projectIdExisting =  UUID.fromString("99411c88-a9ab-4d20-928e-eb0128bf8f0c");
        projectIdNonExisting = UUID.fromString("99411c88-a9ab-4d20-928e-eb0128bf8f0d");
    }

    @Test
    @Order(1)
    @SneakyThrows
    void givenPayloadProjectWhenCreateThenReturnCreatedProject() {
        var createProjectInputDTO = CreateProjectInputDTO.builder()
                .name("New Project")
                .build();
        String payload = objectMapper.writeValueAsString(createProjectInputDTO);

        mockMvc.perform(post(URL)
                        .content(payload)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(createProjectInputDTO.name()))
                .andExpect(jsonPath("$.projectExternalId").isNotEmpty())
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @Order(2)
    @SneakyThrows
    void givenPayloadProjectWhenCreateThenNotSaveDueToExistingName() {
        var createProjectInputDTO = CreateProjectInputDTO.builder()
                .name("Project A")
                .build();
        String payload = objectMapper.writeValueAsString(createProjectInputDTO);

        mockMvc.perform(post(URL)
                        .content(payload)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value("A project with the name 'Project A' already exists."))
                .andExpect(jsonPath("$.message").value("A business rule has been violated."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(3)
    @SneakyThrows
    void givenPayloadProjectWhenCreateThenThrowDueToNameIsEmpty() {
        var createProjectInputDTO = CreateProjectInputDTO.builder()
                .build();
        String payload = objectMapper.writeValueAsString(createProjectInputDTO);

        mockMvc.perform(post(URL)
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
                .andExpect(jsonPath("$.fields[0].field").value("name"))
                .andExpect(jsonPath("$.fields[0].message").value("Name of the project is required"));
    }

    @Test
    @Order(4)
    @SneakyThrows
    void givenPayloadProjectWhenCreateThenThrowDueToNameExceedsMaxLength() {
        var createProjectInputDTO = CreateProjectInputDTO.builder()
                .name(RandomStringUtils.secure().nextAlphabetic(256))
                .build();
        String payload = objectMapper.writeValueAsString(createProjectInputDTO);

        mockMvc.perform(post(URL)
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
                .andExpect(jsonPath("$.fields[0].field").value("name"))
                .andExpect(jsonPath("$.fields[0].message").value("Name of the project must be between 1 and 255 characters long"));
    }

    @Test
    @Order(5)
    @SneakyThrows
    void givenProjectExternalIdExistingWhenRetrieveByProjectExternalIdThenReturnProjectExisting() {
        mockMvc.perform(get(URL.concat("/{projectExternalId}"), projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectExternalId").value(projectIdExisting.toString()))
                .andExpect(jsonPath("$.name").value("Project A"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @Order(6)
    @SneakyThrows
    void givenProjectExternalIdNonExistingWhenRetrieveByProjectExternalIdThenReturnNotFound() {
        mockMvc.perform(get(URL.concat("/{projectExternalId}"), projectIdNonExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value(String.format("Project with ID %s not found.", projectIdNonExisting.toString())))
                .andExpect(jsonPath("$.message").value("The requested resource could not be found."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(7)
    @SneakyThrows
    void givenPayloadProjectWithProjectExternalIdExistingWhenUpdateThenReturnProjectUpdate() {
        var updateProjectInputDTO = UpdateProjectInputDTO.builder()
                .name("Project A Updated")
                .build();
        String payload = objectMapper.writeValueAsString(updateProjectInputDTO);

        mockMvc.perform(put(URL.concat("/{projectExternalId}"), projectIdExisting)
                        .content(payload)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectExternalId").value(projectIdExisting.toString()))
                .andExpect(jsonPath("$.name").value(updateProjectInputDTO.name()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @Order(8)
    @SneakyThrows
    void givenPayloadProjectWithProjectExternalIdNonExistingWhenUpdateThenReturnNotFound() {
        var updateProjectInputDTO = UpdateProjectInputDTO.builder()
                .name("Project A Updated")
                .build();
        String payload = objectMapper.writeValueAsString(updateProjectInputDTO);

        mockMvc.perform(put(URL.concat("/{projectExternalId}"), projectIdNonExisting)
                        .content(payload)
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
    @Order(9)
    @SneakyThrows
    void givenPayloadProjectWithProjectExternalIdExistingWhenUpdateThenReturnNotSaveDueToExistingName() {
        // Create another project with name "Project B" to test the update with existing name
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name("Project B")
                .build();
        projectEntity = projectRepository.saveAndFlush(projectEntity);

        UpdateProjectInputDTO updateProjectInputDTO = UpdateProjectInputDTO.builder()
                .name("Project A")
                .build();
        String payload = objectMapper.writeValueAsString(updateProjectInputDTO);

        mockMvc.perform(put(URL.concat("/{projectExternalId}"), projectEntity.getProjectExternalId())
                        .content(payload)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Business Rule Violation"))
                .andExpect(jsonPath("$.detail").value("A project with the name 'Project A' already exists."))
                .andExpect(jsonPath("$.message").value("A business rule has been violated."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(10)
    @SneakyThrows
    void givenPayloadProjectWithProjectExternalIdExistingWhenUpdateThenThrowDueToNameIsEmpty() {
        var updateProjectInputDTO = UpdateProjectInputDTO.builder()
                .build();
        String payload = objectMapper.writeValueAsString(updateProjectInputDTO);

        mockMvc.perform(put(URL.concat("/{projectExternalId}"), projectIdExisting)
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
                .andExpect(jsonPath("$.fields[0].field").value("name"))
                .andExpect(jsonPath("$.fields[0].message").value("Name of the project is required"));
    }

    @Test
    @Order(11)
    @SneakyThrows
    void givenPayloadProjectWithProjectExternalIdExistingWhenUpdateThenThrowDueToNameExceedsMaxLength() {
        var updateProjectInputDTO = UpdateProjectInputDTO.builder()
                .name(RandomStringUtils.secure().nextAlphabetic(256))
                .build();
        String payload = objectMapper.writeValueAsString(updateProjectInputDTO);

        mockMvc.perform(put(URL.concat("/{projectExternalId}"), projectIdExisting)
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
                .andExpect(jsonPath("$.fields[0].field").value("name"))
                .andExpect(jsonPath("$.fields[0].message").value("Name of the project must be between 1 and 255 characters long"));
    }

    @Test
    @Order(12)
    @SneakyThrows
    void givenProjectExternalIdExistingWhenDeleteThenReturnNoContent() {
        mockMvc.perform(delete(URL.concat("/{projectExternalId}"), projectIdExisting)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(13)
    @SneakyThrows
    void givenProjectExternalIdNonExistingWhenDeleteThenReturnNotFound() {
        mockMvc.perform(delete(URL.concat("/{projectExternalId}"), projectIdNonExisting)
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
    @Order(14)
    @SneakyThrows
    void givenProjectExternalIdExistingWhenDeleteThenThrowDueToProjectInUse() {
        // Create another project and user to associate with the existing project to make it in use
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name("Project C")
                .build();
        projectEntity = projectRepository.saveAndFlush(projectEntity);

        // Associate the user with the existing project to make it in use
        UserEntity userEntity = UserEntityStub.createUserEntity(projectEntity);
        userRepository.saveAndFlush(userEntity);

        mockMvc.perform(delete(URL.concat("/{projectExternalId}"), projectEntity.getProjectExternalId())
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Entity In Use"))
                .andExpect(jsonPath("$.detail").value(String.format("Project with ID %s cannot be removed as it is in use.", projectEntity.getProjectExternalId().toString())))
                .andExpect(jsonPath("$.message").value("The entity is currently in use and cannot be modified or deleted."))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(14)
    @SneakyThrows
    void givenProjectFilterDtoWhenListAllThenReturnProject() {
        ProjectEntity projectEntity = ProjectEntity.builder()
                .name("Project C")
                .build();
        projectEntity = projectRepository.saveAndFlush(projectEntity);

        var projectFilterDTO = ProjectFilterDTO.builder()
                .projectExternalId(projectEntity.getProjectExternalId())
                .name(projectEntity.getName())
                .createdAtTo(projectEntity.getCreatedAt())
                .createdAtFrom(projectEntity.getCreatedAt())
                .build();

        String payload = objectMapper.writeValueAsString(projectFilterDTO);

        String url = URL.concat("?sort=createdAt,DESC");
        mockMvc.perform(get(url)
                        .contentType(MEDIA_TYPE_JSON)
                        .accept(MEDIA_TYPE_JSON)
                        .content(payload)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].projectExternalId").value(projectEntity.getProjectExternalId().toString()))
                .andExpect(jsonPath("$.content[0].name").value(projectEntity.getName()))
                .andExpect(jsonPath("$.content[0].createdAt").value(projectEntity.getCreatedAt().toString()))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }
}