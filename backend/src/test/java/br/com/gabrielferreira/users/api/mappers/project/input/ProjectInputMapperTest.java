package br.com.gabrielferreira.users.api.mappers.project.input;

import br.com.gabrielferreira.users.api.dtos.filter.project.ProjectFilterDTO;
import br.com.gabrielferreira.users.api.dtos.input.project.CreateProjectInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.project.UpdateProjectInputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.project.ProjectFilter;
import br.com.gabrielferreira.users.stub.project.ProjectFilterDTOStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Tests for ProjectInputMapper")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectInputMapperTest {

    private final ProjectInputMapper mapper = ProjectInputMapper.INSTANCE;

    @Test
    @Order(1)
    void givenCreateProjectInputDTOWhenToProjectEntityThenReturnProjectCreated() {
        var createProjectInputDTO = CreateProjectInputDTO.builder()
                .name("Project A")
                .build();

        ProjectEntity result = mapper.toProjectEntity(createProjectInputDTO);
        assertNotNull(result);
        assertEquals(createProjectInputDTO.name(), result.getName());
    }

    @Test
    @Order(2)
    void givenUpdateProjectInputDTOWhenToProjectEntityThenReturnProjectCreated() {
        var updateProjectInputDTO = UpdateProjectInputDTO.builder()
                .name("Project A")
                .build();

        ProjectEntity result = mapper.toProjectEntity(updateProjectInputDTO);
        assertNotNull(result);
        assertEquals(updateProjectInputDTO.name(), result.getName());
    }

    @Test
    @Order(3)
    void givenProjectFilterDTOWhenToProjectFilterThenReturnProjectCreated() {
        ProjectFilterDTO projectFilterDTO = ProjectFilterDTOStub.createProjectFilterDto();

        ProjectFilter result = mapper.toProjectFilter(projectFilterDTO);
        assertNotNull(result);
        assertEquals(projectFilterDTO.projectExternalId(), result.projectExternalId());
        assertEquals(projectFilterDTO.name(), result.name());
        assertEquals(projectFilterDTO.createdAtFrom(), result.createdAtFrom());
        assertEquals(projectFilterDTO.createdAtTo(), result.createdAtTo());
    }
}