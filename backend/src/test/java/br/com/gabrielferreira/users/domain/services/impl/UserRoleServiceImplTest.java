package br.com.gabrielferreira.users.domain.services.impl;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.repositories.RoleRepository;
import br.com.gabrielferreira.users.domain.repositories.filter.role.RoleFilter;
import br.com.gabrielferreira.users.domain.services.RoleService;
import br.com.gabrielferreira.users.domain.services.UserService;
import br.com.gabrielferreira.users.stub.project.ProjectEntityStub;
import br.com.gabrielferreira.users.stub.role.RoleEntityStub;
import br.com.gabrielferreira.users.stub.user.UserEntityStub;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Unit tests for UserRoleServiceImplTest")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRoleServiceImplTest {

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    @Mock
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    private UUID userExternalId;

    private UUID roleExternalId;

    private UUID projectExternalId;

    private UserEntity userEntity;

    private RoleEntity roleEntity;

    private OffsetDateTime offsetDateTime;

    @BeforeEach
    void setUp() {
        userExternalId = UUID.fromString("3340ec9b-b2ab-460a-a47a-2fa49a7d0c94");
        roleExternalId = UUID.fromString("2e570ff5-3870-424e-a490-f041ff829cdc");
        projectExternalId = UUID.fromString("0d2aa38b-5fae-4125-8cdd-b8f75cf8a6de");

        ProjectEntity projectEntity = ProjectEntityStub.createProjectEntity("Project Abc", UUID.fromString("df367ff1-cc06-4d04-bbd8-46aa826db630"));
        roleEntity = RoleEntityStub.createRoleEntity(
                "Role admin",
                "ROLE_ADMIN",
                roleExternalId,
                projectEntity
        );
        userEntity = UserEntityStub.createUserEntity(projectEntity);
        offsetDateTime = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    }

    @Test
    @Order(1)
    void givenUserExternalIdWithProjectExternalIdWhenAssociateRoleThenShouldAssociateRoleToUser() {
        userEntity.setRoles(new ArrayList<>());

        when(userService.getOneUser(userExternalId, projectExternalId)).thenReturn(userEntity);
        when(roleService.getOneRole(roleExternalId, projectExternalId)).thenReturn(roleEntity);

        assertTrue(userEntity.getRoles().isEmpty());
        assertDoesNotThrow(() -> userRoleService.associateRole(userExternalId, roleExternalId, projectExternalId));
        assertFalse(userEntity.getRoles().isEmpty());
        assertTrue(userEntity.getRoles().stream().anyMatch(role -> role.getRoleExternalId().equals(roleExternalId)));
    }

    @Test
    @Order(2)
    void givenUserExternalIdWithProjectExternalIdWhenAssociateRoleThenNotShouldAssociateRoleToUser() {
        userEntity.setRoles(new ArrayList<>());
        userEntity.getRoles().add(roleEntity);

        when(userService.getOneUser(userExternalId, projectExternalId)).thenReturn(userEntity);
        when(roleService.getOneRole(roleExternalId, projectExternalId)).thenReturn(roleEntity);

        assertFalse(userEntity.getRoles().isEmpty());
        assertTrue(userEntity.getRoles().stream().anyMatch(role -> role.getRoleExternalId().equals(roleExternalId)));
        assertDoesNotThrow(() -> userRoleService.associateRole(userExternalId, roleExternalId, projectExternalId));
        assertFalse(userEntity.getRoles().isEmpty());
        assertTrue(userEntity.getRoles().stream().anyMatch(role -> role.getRoleExternalId().equals(roleExternalId)));
    }

    @Test
    @Order(3)
    void givenUserExternalIdWithProjectExternalIdWhenDisassociateRoleThenShouldDisassociateRoleToUser() {
        userEntity.setRoles(new ArrayList<>());
        userEntity.getRoles().add(roleEntity);

        when(userService.getOneUser(userExternalId, projectExternalId)).thenReturn(userEntity);
        when(roleService.getOneRole(roleExternalId, projectExternalId)).thenReturn(roleEntity);

        assertFalse(userEntity.getRoles().isEmpty());
        assertTrue(userEntity.getRoles().stream().anyMatch(role -> role.getRoleExternalId().equals(roleExternalId)));
        assertDoesNotThrow(() -> userRoleService.disassociateRole(userExternalId, roleExternalId, projectExternalId));
        assertTrue(userEntity.getRoles().isEmpty());
    }

    @Test
    @Order(4)
    void givenUserExternalIdWithProjectExternalIdWhenDisassociateRoleThenNotShouldDisassociateRoleToUser() {
        userEntity.setRoles(new ArrayList<>());

        when(userService.getOneUser(userExternalId, projectExternalId)).thenReturn(userEntity);
        when(roleService.getOneRole(roleExternalId, projectExternalId)).thenReturn(roleEntity);

        assertTrue(userEntity.getRoles().isEmpty());
        assertDoesNotThrow(() -> userRoleService.disassociateRole(userExternalId, roleExternalId, projectExternalId));
        assertTrue(userEntity.getRoles().isEmpty());
    }

    @Test
    @Order(5)
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

        Page<RoleEntity> resultPage = userRoleService.listRolesByUser(userExternalId, pageRequest, roleFilter);
        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());
        verify(roleRepository).findAll(ArgumentMatchers.<Specification<RoleEntity>>any(), eq(pageRequest));
    }
}