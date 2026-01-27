package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.ProjectNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.ProjectRepository;
import br.com.gabrielferreira.users.domain.repositories.projection.SummaryProjectProjection;
import br.com.gabrielferreira.users.stub.ProjectEntityStub;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit tests for ProjectServiceImpl")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProjectServiceImplTest {

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Mock
    private ProjectRepository projectRepository;

    private UUID projectExternalId;

    @BeforeEach
    void setUp() {
        projectExternalId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    }

    @Test
    @Order(1)
    void givenProjectEntityWhenSaveThenReturnProjectEntityCreated() {
        ProjectEntity projectEntity = ProjectEntityStub.createProjectEntity("Project A");
        when(projectRepository.findOneByName(projectEntity.getName()))
                .thenReturn(Optional.empty());

        ProjectEntity projectCreated = ProjectEntityStub.createProjectEntityWithMoreInfo(projectEntity.getName());
        when(projectRepository.save(projectEntity))
                .thenReturn(projectCreated);

        ProjectEntity projectEntitySaved = projectService.save(projectEntity);

        assertNotNull(projectEntitySaved);
        assertEquals(projectCreated, projectEntitySaved);
        verify(projectRepository).findOneByName(projectEntity.getName());
        verify(projectRepository).save(projectEntity);
    }

    @Test
    @Order(2)
    void givenProjectEntityWhenSaveThenNotSaveDueToExistingName() {
        ProjectEntity projectEntity = ProjectEntityStub.createProjectEntity("Project A");
        SummaryProjectProjection existingProject = mock(SummaryProjectProjection.class);
        when(projectRepository.findOneByName(projectEntity.getName()))
                .thenReturn(Optional.of(existingProject));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> projectService.save(projectEntity));

        assertEquals("A project with the name 'Project A' already exists.", exception.getMessage());
        verify(projectRepository).findOneByName(projectEntity.getName());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @Order(3)
    void givenProjectExternalIdWhenGetOneProjectThenReturnProjectEntity() {
        ProjectEntity projectEntityFound = ProjectEntityStub.createProjectEntityWithMoreInfo("Project B");
        projectEntityFound.setProjectExternalId(projectExternalId);

        when(projectRepository.findOneByProjectExternalId(projectExternalId))
                .thenReturn(Optional.of(projectEntityFound));

        ProjectEntity projectEntity = projectService.getOneProject(projectExternalId);

        assertNotNull(projectEntity);
        assertEquals(projectEntityFound, projectEntity);
        verify(projectRepository).findOneByProjectExternalId(projectExternalId);
    }

    @Test
    @Order(4)
    void givenProjectExternalIdWhenGetOneProjectThenThrowProjectNotFoundException() {
        when(projectRepository.findOneByProjectExternalId(projectExternalId))
                .thenReturn(Optional.empty());

        ProjectNotFoundException exception = assertThrows(ProjectNotFoundException.class, () -> projectService.getOneProject(projectExternalId));

        assertEquals("Project with ID 123e4567-e89b-12d3-a456-426614174000 not found", exception.getMessage());
        verify(projectRepository).findOneByProjectExternalId(projectExternalId);
    }
}