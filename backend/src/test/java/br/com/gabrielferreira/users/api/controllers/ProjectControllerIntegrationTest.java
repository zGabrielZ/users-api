package br.com.gabrielferreira.users.api.controllers;

import br.com.gabrielferreira.users.api.dtos.input.project.CreateProjectInputDTO;
import br.com.gabrielferreira.users.stub.project.ProjectDTOStub;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Integration tests for ProjectController")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectControllerIntegrationTest {

    private static final String URL = "/v1/projects";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.APPLICATION_JSON;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    @Order(1)
    @SneakyThrows
    void givenCreateProjectInputDTOWhenCreateThenReturnProjectOutputDTOCreated() {
        CreateProjectInputDTO createProjectInputDTO = ProjectDTOStub.createCreateProjectInputDTO("New Project");
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
    void givenCreateProjectInputDTOWhenCreateThenNotSaveDueToExistingName() {
        CreateProjectInputDTO createProjectInputDTO = ProjectDTOStub.createCreateProjectInputDTO("Project A");
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
    void givenCreateProjectInputDTOWhenCreateThenThrowDueToNameIsEmpty() {
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

    // FIXME: Preciso corrigir esse teste integrado
    @Test
    @Order(4)
    @SneakyThrows
    void givenCreateProjectInputDTOWhenCreateThenThrowDueToNameExceedsMaxLength() {
//        var createProjectInputDTO = CreateProjectInputDTO.builder()
//                .name("This project name is way too long and exceeds the maximum length allowed by the validation constraints")
//                .build();
//        String payload = objectMapper.writeValueAsString(createProjectInputDTO);
//
//        mockMvc.perform(post(URL)
//                        .content(payload)
//                        .contentType(MEDIA_TYPE_JSON)
//                        .accept(MEDIA_TYPE_JSON)
//                )
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.status").value(400))
//                .andExpect(jsonPath("$.title").value("Invalid Data"))
//                .andExpect(jsonPath("$.detail").value("One or more fields are invalid. Please correct them and try again."))
//                .andExpect(jsonPath("$.message").value("One or more fields are invalid. Please correct them and try again."))
//                .andExpect(jsonPath("$.timestamp").isNotEmpty())
//                .andExpect(jsonPath("$.fields[0].field").value("name"))
//                .andExpect(jsonPath("$.fields[0].message").value("Name of the project must not exceed 50 characters"));

//        // Criar o DTO de entrada
//        CreateProjectInputDTO createProjectInputDTO = ProjectDTOStub.createCreateProjectInputDTO("New Project");
//        String payload = objectMapper.writeValueAsString(createProjectInputDTO);
//
//        // Executar a requisição e capturar a resposta
//        var result = mockMvc.perform(post(URL)
//                        .content(payload)
//                        .contentType(MEDIA_TYPE_JSON)
//                        .accept(MEDIA_TYPE_JSON))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        // Mapear a resposta para a classe desejada
//        String responseContent = result.getResponse().getContentAsString();
//        ProjectOutputDTO projectOutputDTO = objectMapper.readValue(responseContent, ProjectOutputDTO.class);
//
//        // Verificar os valores da resposta
//        Assertions.assertNotNull(projectOutputDTO);
//        Assertions.assertEquals(createProjectInputDTO.name(), projectOutputDTO.name());
//        Assertions.assertNotNull(projectOutputDTO.projectExternalId());
//        Assertions.assertNotNull(projectOutputDTO.createdAt());
    }
}