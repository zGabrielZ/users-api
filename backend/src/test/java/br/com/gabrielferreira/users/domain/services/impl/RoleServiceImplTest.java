package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.exceptions.BusinessRuleException;
import br.com.gabrielferreira.users.domain.exceptions.EntityInUseException;
import br.com.gabrielferreira.users.domain.exceptions.RoleNotFoundException;
import br.com.gabrielferreira.users.domain.repositories.RoleRepository;
import br.com.gabrielferreira.users.domain.repositories.filter.role.RoleFilter;
import br.com.gabrielferreira.users.domain.repositories.projection.role.SummaryRoleProjection;
import br.com.gabrielferreira.users.domain.services.ProjectService;
import br.com.gabrielferreira.users.stub.project.ProjectEntityStub;
import br.com.gabrielferreira.users.stub.role.RoleEntityStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Unit tests for RoleServiceImplTest")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProjectService projectService;

    private UUID roleExternalId;

    private UUID projectExternalId;

    private ProjectEntity projectEntity;

    private OffsetDateTime offsetDateTime;

    @BeforeEach
    void setUp() {
        roleExternalId = UUID.fromString("d353a29b-bbfd-4c23-836f-cb0353c32591");
        projectExternalId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        projectEntity = ProjectEntityStub.createProjectEntity("Project A", projectExternalId);
        offsetDateTime = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    }

    @Test
    @Order(1)
    void givenRoleEntityWhenSaveThenReturnRoleEntityCreated() {
        var role = RoleEntity.builder()
                .description("Administrator")
                .authority("ROLE_ADMIN")
                .build();

        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        when(roleRepository.findOneByAuthorityAndProjectExternalId(role.getAuthority(), projectExternalId))
                .thenReturn(Optional.empty());

        RoleEntity roleEntityCreated = RoleEntityStub.createRoleEntity(
                role.getDescription(),
                role.getAuthority(),
                roleExternalId,
                projectEntity
        );
        when(roleRepository.save(role))
                .thenReturn(roleEntityCreated);

        RoleEntity roleEntitySaved = roleService.save(role, projectExternalId);

        assertNotNull(roleEntitySaved);
        assertEquals(roleEntityCreated, roleEntitySaved);
        verify(projectService).getOneProject(projectExternalId);
        verify(roleRepository).findOneByAuthorityAndProjectExternalId(role.getAuthority(), projectExternalId);
        verify(roleRepository).save(role);
    }

    @Test
    @Order(2)
    void givenRoleEntityWhenSaveThenNotSaveDueToExistingAuthority() {
        var role = RoleEntity.builder()
                .description("Administrator")
                .authority("ROLE_ADMIN")
                .build();

        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        SummaryRoleProjection existingRole = mock(SummaryRoleProjection.class);
        when(roleRepository.findOneByAuthorityAndProjectExternalId(role.getAuthority(), projectExternalId))
                .thenReturn(Optional.of(existingRole));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> roleService.save(role, projectExternalId));

        String expectedMessage = String.format("A role with the authority '%s' already exists in the project '%s'.", role.getAuthority(), projectEntity.getName());
        assertEquals(expectedMessage, exception.getMessage());
        verify(projectService).getOneProject(projectExternalId);
        verify(roleRepository).findOneByAuthorityAndProjectExternalId(role.getAuthority(), projectExternalId);
        verify(roleRepository, never()).save(role);
    }

    @Test
    @Order(3)
    void givenRoleExternalIdWithProjectExternalIdWhenGetOneRoleThenReturnRoleEntity() {
        RoleEntity roleEntityFound = RoleEntityStub.createRoleEntity(
                "Role 2",
                "ROLE_TEST",
                roleExternalId,
                projectEntity
        );

        when(roleRepository.findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId))
                .thenReturn(Optional.of(roleEntityFound));

        RoleEntity roleEntity = roleService.getOneRole(roleExternalId, projectExternalId);

        assertNotNull(roleEntity);
        assertEquals(roleEntityFound, roleEntity);
        verify(roleRepository).findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId);
    }

    @Test
    @Order(4)
    void givenRoleExternalIdWithProjectExternalIdWhenGetOneRoleThenThrowRoleNotFoundException() {
        when(roleRepository.findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId))
                .thenReturn(Optional.empty());

        RoleNotFoundException roleNotFoundException = assertThrows(RoleNotFoundException.class, () -> roleService.getOneRole(roleExternalId, projectExternalId));

        String expectedMessage = String.format("Role with ID %s not found for Project with ID %s", roleExternalId, projectExternalId);
        assertEquals(expectedMessage, roleNotFoundException.getMessage());
        verify(roleRepository).findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId);
    }

    @Test
    @Order(5)
    void givenRoleEntityWithRoleExternalIdWhenUpdateRoleThenReturnRoleEntityUpdated() {
        var role = RoleEntity.builder()
                .description("Administrator Administrator Administrator")
                .authority("ROLE_ADMIN_ADMIN")
                .build();

        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        RoleEntity roleEntityFound = RoleEntityStub.createRoleEntity(
                "Administrator",
                "ROLE_ADMIN",
                roleExternalId,
                projectEntity
        );

        when(roleRepository.findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId))
                .thenReturn(Optional.of(roleEntityFound));

        when(roleRepository.findOneByAuthorityAndProjectExternalId(role.getAuthority(), projectExternalId))
                .thenReturn(Optional.empty());

        RoleEntity roleEntityUpdated = RoleEntityStub.createRoleEntity(role.getAuthority(), role.getDescription(), roleExternalId, projectEntity);
        roleEntityFound.setAuthority(role.getAuthority());
        roleEntityFound.setDescription(role.getDescription());
        when(roleRepository.save(roleEntityFound))
                .thenReturn(roleEntityUpdated);

        RoleEntity roleEntityResult = roleService.update(roleExternalId, role, projectExternalId);
        assertNotNull(roleEntityResult);
        assertEquals(roleEntityUpdated, roleEntityResult);
        verify(projectService).getOneProject(projectExternalId);
        verify(roleRepository).findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId);
        verify(roleRepository).findOneByAuthorityAndProjectExternalId(role.getAuthority(), projectExternalId);
        verify(roleRepository).save(roleEntityFound);
    }

    @Test
    @Order(6)
    void givenRoleEntityWithRoleExternalIdWhenUpdateRoleThenThrowDueToExistingAuthority() {
        var role = RoleEntity.builder()
                .description("Administrator Administrator Administrator")
                .authority("ROLE_ADMIN_ADMIN")
                .build();

        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        RoleEntity roleEntityFound = RoleEntityStub.createRoleEntity(
                "Administrator",
                "ROLE_ADMIN",
                roleExternalId,
                projectEntity
        );

        when(roleRepository.findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId))
                .thenReturn(Optional.of(roleEntityFound));

        SummaryRoleProjection existingRole = mock(SummaryRoleProjection.class);
        when(roleRepository.findOneByAuthorityAndProjectExternalId(role.getAuthority(), projectExternalId))
                .thenReturn(Optional.of(existingRole));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> roleService.update(roleExternalId, role, projectExternalId));

        String expectedMessage = String.format("A role with the authority '%s' already exists in the project '%s'.", role.getAuthority(), projectEntity.getName());
        assertEquals(expectedMessage, exception.getMessage());
        verify(projectService).getOneProject(projectExternalId);
        verify(roleRepository).findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId);
        verify(roleRepository).findOneByAuthorityAndProjectExternalId(role.getAuthority(), projectExternalId);
        verify(roleRepository, never()).save(roleEntityFound);
    }

    @Test
    @Order(7)
    void givenRoleExternalIdWithProjectExternalIdWhenDeleteRoleThenReturnSuccess() {
        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        RoleEntity roleEntityFound = RoleEntityStub.createRoleEntity(
                "Administrator",
                "ROLE_ADMIN",
                roleExternalId,
                projectEntity
        );

        when(roleRepository.findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId))
                .thenReturn(Optional.of(roleEntityFound));

        doNothing().when(roleRepository).delete(roleEntityFound);
        doNothing().when(roleRepository).flush();

        assertDoesNotThrow(() -> roleService.delete(roleExternalId, projectExternalId));
        verify(projectService).getOneProject(projectExternalId);
        verify(roleRepository).findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId);
        verify(roleRepository).delete(roleEntityFound);
        verify(roleRepository).flush();
    }

    @Test
    @Order(8)
    void givenRoleExternalIdWithProjectExternalIdWhenDeleteRoleThenThrowDueToUsedEntity() {
        when(projectService.getOneProject(projectExternalId))
                .thenReturn(projectEntity);

        RoleEntity roleEntityFound = RoleEntityStub.createRoleEntity(
                "Administrator",
                "ROLE_ADMIN",
                roleExternalId,
                projectEntity
        );

        doThrow(DataIntegrityViolationException.class).when(roleRepository)
                .delete(roleEntityFound);
        when(roleRepository.findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId))
                .thenReturn(Optional.of(roleEntityFound));

        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> roleService.delete(roleExternalId, projectExternalId));
        String expectedMessage = String.format("Role with ID %s cannot be removed as it is in use.", roleExternalId);
        assertEquals(expectedMessage, exception.getMessage());
        verify(projectService).getOneProject(projectExternalId);
        verify(roleRepository).findOneByRoleExternalIdAndProjectExternalId(roleExternalId, projectExternalId);
        verify(roleRepository).delete(roleEntityFound);
        verify(roleRepository, never()).flush();
    }

    @Test
    @Order(9)
    void givenRoleFilterWithPageableWhenGetAllRolesThenReturnPageOfRoleEntity() {
        var roleFilter = RoleFilter.builder()
                .roleExternalId(roleExternalId)
                .description("Role")
                .authority("ROLE_")
                .createdAtFrom(offsetDateTime)
                .createdAtTo(offsetDateTime)
                .projectExternalId(projectExternalId)
                .build();

        var pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("name")));
        Page<RoleEntity> roleEntityPage = RoleEntityStub.createPageOfRoleEntities(pageRequest);
        when(roleRepository.findAll(ArgumentMatchers.<Specification<RoleEntity>>any(), eq(pageRequest)))
                .thenReturn(roleEntityPage);

        Page<RoleEntity> resultPage = roleService.getAllRoles(roleFilter, pageRequest);
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        verify(roleRepository).findAll(ArgumentMatchers.<Specification<RoleEntity>>any(), eq(pageRequest));
    }
}