package br.com.gabrielferreira.users.api.mappers.project.output;

import br.com.gabrielferreira.users.api.dtos.output.page.PageResponse;
import br.com.gabrielferreira.users.api.dtos.output.project.ProjectOutputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.stub.project.ProjectEntityStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tests for ProjectOutputMapper")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectOutputMapperTest {

    private final ProjectOutputMapper mapper = ProjectOutputMapper.INSTANCE;

    private ProjectEntity projectEntity;

    @BeforeEach
    void setUp() {
        projectEntity = ProjectEntityStub.createProjectEntity(null, null);
    }

    @Test
    @Order(1)
    void givenProjectEntityWhenToProjectOutputDtoThenReturnProjectCreated() {
        ProjectOutputDTO result = mapper.toProjectOutputDto(projectEntity);
        assertNotNull(result);
        assertEquals(projectEntity.getProjectExternalId(), result.projectExternalId());
        assertEquals(projectEntity.getName(), result.name());
        assertEquals(projectEntity.getCreatedAt(), result.createdAt());
    }

    @Test
    @Order(2)
    void givenPageProjectEntitiesEmptyWhenToPageDtoThenReturnProjectCreated() {
        List<ProjectEntity> projectEntityList = new ArrayList<>();
        Page<ProjectEntity> projectEntities = new PageImpl<>(projectEntityList, PageRequest.of(0, 1), 0);

        PageResponse<ProjectOutputDTO> result = mapper.toPageDto(projectEntities);
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(projectEntities.getNumber(), result.getNumber());
        assertEquals(projectEntities.getSize(), result.getSize());
        assertEquals(projectEntities.getTotalElements(), result.getTotalElements());
        assertEquals(projectEntities.getTotalPages(), result.getTotalPages());
    }

    @Test
    @Order(3)
    void givenPageProjectEntitiesNotEmptyWhenToPageDtoThenReturnProjectCreated() {
        List<ProjectEntity> projectEntityList = new ArrayList<>();
        projectEntityList.add(projectEntity);
        Page<ProjectEntity> projectEntities = new PageImpl<>(projectEntityList, PageRequest.of(0, 1), projectEntityList.size());

        PageResponse<ProjectOutputDTO> result = mapper.toPageDto(projectEntities);
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(projectEntityList.get(0).getName(), result.getContent().get(0).name());
        assertEquals(projectEntityList.get(0).getProjectExternalId(), result.getContent().get(0).projectExternalId());
        assertEquals(projectEntityList.get(0).getCreatedAt(), result.getContent().get(0).createdAt());
        assertEquals(projectEntities.getNumber(), result.getNumber());
        assertEquals(projectEntities.getSize(), result.getSize());
        assertEquals(projectEntities.getTotalElements(), result.getTotalElements());
        assertEquals(projectEntities.getTotalPages(), result.getTotalPages());
    }
}