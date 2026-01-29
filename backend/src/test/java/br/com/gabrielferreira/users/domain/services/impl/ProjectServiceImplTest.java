package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.EntityInUseException;
import br.com.gabrielferreira.users.domain.exceptions.ProjectNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.ProjectRepository;
import br.com.gabrielferreira.users.domain.repositories.filter.ProjectFilter;
import br.com.gabrielferreira.users.domain.repositories.projection.project.SummaryProjectProjection;
import br.com.gabrielferreira.users.stub.ProjectEntityStub;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
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

    private OffsetDateTime offsetDateTime;

    @BeforeEach
    void setUp() {
        projectExternalId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        offsetDateTime = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    }

    @Test
    @Order(1)
    void givenProjectEntityWhenSaveThenReturnProjectEntityCreated() {
        ProjectEntity projectEntity = ProjectEntityStub.createProjectEntity("Project A");
        when(projectRepository.findOneByName(projectEntity.getName()))
                .thenReturn(Optional.empty());

        ProjectEntity projectCreated = ProjectEntityStub.createProjectEntityWithMoreInfo(projectEntity.getName(), projectExternalId);
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
        ProjectEntity projectEntityFound = ProjectEntityStub.createProjectEntityWithMoreInfo("Project B", projectExternalId);
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

        assertEquals("Project with ID 123e4567-e89b-12d3-a456-426614174000 not found.", exception.getMessage());
        verify(projectRepository).findOneByProjectExternalId(projectExternalId);
    }

    @Test
    @Order(5)
    void givenProjectEntityWithProjectExternalIdWhenUpdateProjectThenReturnProjectEntityUpdated() {
        ProjectEntity projectEntity = ProjectEntityStub.createProjectEntity("Project A");

        ProjectEntity projectEntityFound = ProjectEntityStub.createProjectEntityWithMoreInfo("Project B", projectExternalId);
        when(projectRepository.findOneByProjectExternalId(projectExternalId))
                .thenReturn(Optional.of(projectEntityFound));

        when(projectRepository.findOneByName(projectEntity.getName()))
                .thenReturn(Optional.empty());

        ProjectEntity projectEntityUpdated = ProjectEntityStub.createProjectEntityWithMoreInfo(projectEntity.getName(), projectExternalId);
        projectEntityFound.setName(projectEntity.getName());
        when(projectRepository.save(projectEntityFound))
                .thenReturn(projectEntityUpdated);

        ProjectEntity projectEntityResult = projectService.update(projectExternalId, projectEntity);
        assertNotNull(projectEntityResult);
        assertEquals(projectEntityUpdated, projectEntityResult);
        verify(projectRepository).findOneByProjectExternalId(projectExternalId);
        verify(projectRepository).findOneByName(projectEntity.getName());
        verify(projectRepository).save(projectEntityFound);
    }

    @Test
    @Order(6)
    void givenProjectEntityWithProjectExternalIdWhenUpdateProjectThenThrowDueToExistingName() {
        ProjectEntity projectEntity = ProjectEntityStub.createProjectEntity("Project A");

        ProjectEntity projectEntityFound = ProjectEntityStub.createProjectEntityWithMoreInfo("Project B", projectExternalId);
        when(projectRepository.findOneByProjectExternalId(projectExternalId))
                .thenReturn(Optional.of(projectEntityFound));

        SummaryProjectProjection existingProject = mock(SummaryProjectProjection.class);
        when(projectRepository.findOneByName(projectEntity.getName()))
                .thenReturn(Optional.of(existingProject));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> projectService.update(projectExternalId, projectEntity));

        assertEquals("A project with the name 'Project A' already exists.", exception.getMessage());
        verify(projectRepository).findOneByProjectExternalId(projectExternalId);
        verify(projectRepository).findOneByName(projectEntity.getName());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @Order(7)
    void givenProjectExternalIdWhenDeleteProjectThenSuccess() {
        ProjectEntity projectEntityFound = ProjectEntityStub.createProjectEntityWithMoreInfo("Project B", projectExternalId);
        when(projectRepository.findOneByProjectExternalId(projectExternalId))
                .thenReturn(Optional.of(projectEntityFound));

        doNothing().when(projectRepository).delete(projectEntityFound);
        doNothing().when(projectRepository).flush();

        assertDoesNotThrow(() -> projectService.delete(projectExternalId));
    }

    @Test
    @Order(8)
    void givenProjectExternalIdWhenDeleteProjectThenThrowDueToUsedEntity() {
        ProjectEntity projectEntityFound = ProjectEntityStub.createProjectEntityWithMoreInfo("Project B", projectExternalId);
        when(projectRepository.findOneByProjectExternalId(projectExternalId))
                .thenReturn(Optional.of(projectEntityFound));

        doThrow(DataIntegrityViolationException.class).when(projectRepository)
                .delete(projectEntityFound);

        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> projectService.delete(projectExternalId));
        assertEquals("Project with ID 123e4567-e89b-12d3-a456-426614174000 cannot be removed as it is in use.", exception.getMessage());
    }

    @Test
    @Order(9)
    void givenProjectFilterWithPageableWhenGetAllProjectsThenReturnPageOfProjectEntity() {
        var projectFilter = ProjectFilter.builder()
                .projectExternalId(projectExternalId)
                .name("Project A")
                .createdAtFrom(offsetDateTime)
                .createdAtTo(offsetDateTime)
                .build();

        var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
        Page<ProjectEntity> projectEntityPage = ProjectEntityStub.createPageOfProjectEntities(pageRequest);
        when(projectRepository.findAll(ArgumentMatchers.<Specification<ProjectEntity>>any(), eq(pageRequest)))
                .thenReturn(projectEntityPage);

        Page<ProjectEntity> resultPage = projectService.getAllProjects(projectFilter, pageRequest);
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        verify(projectRepository).findAll(ArgumentMatchers.<Specification<ProjectEntity>>any(), eq(pageRequest));
    }

}