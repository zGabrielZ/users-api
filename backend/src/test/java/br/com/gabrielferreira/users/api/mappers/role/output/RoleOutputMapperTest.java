package br.com.gabrielferreira.users.api.mappers.role.output;

import br.com.gabrielferreira.users.api.dtos.output.page.PageResponse;
import br.com.gabrielferreira.users.api.dtos.output.role.RoleOutputDTO;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.stub.project.ProjectEntityStub;
import br.com.gabrielferreira.users.stub.role.RoleEntityStub;
import org.junit.jupiter.api.BeforeEach;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleOutputMapperTest {

    private final RoleOutputMapper mapper = RoleOutputMapper.INSTANCE;

    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        ProjectEntity projectEntity = ProjectEntityStub.createProjectEntity(null, null);
        roleEntity = RoleEntityStub.createRoleEntity(
                null,
                null,
                null,
                projectEntity
        );
    }

    @Test
    @Order(1)
    void givenRoleEntityWhenToOutputDtoThenReturnRoleOutputCreated() {
        RoleOutputDTO result = mapper.toOutputDto(roleEntity);
        assertNotNull(result);
        assertEquals(roleEntity.getRoleExternalId(), result.roleExternalId());
        assertEquals(roleEntity.getDescription(), result.description());
        assertEquals(roleEntity.getAuthority(), result.authority());
        assertEquals(roleEntity.getCreatedAt(), result.createdAt());
        assertEquals(roleEntity.getProject().getProjectExternalId(), result.project().projectExternalId());
        assertEquals(roleEntity.getProject().getName(), result.project().name());
        assertEquals(roleEntity.getProject().getCreatedAt(), result.project().createdAt());
    }

    @Test
    @Order(2)
    void givenPageRoleEntitiesEmptyWhenToPageDtoThenReturnRoleCreated() {
        List<RoleEntity> roleEntityList = new ArrayList<>();
        Page<RoleEntity> roleEntities = new PageImpl<>(roleEntityList, PageRequest.of(0, 1), 0);

        PageResponse<RoleOutputDTO> result = mapper.toPageDto(roleEntities);
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(roleEntities.getNumber(), result.getNumber());
        assertEquals(roleEntities.getSize(), result.getSize());
        assertEquals(roleEntities.getTotalElements(), result.getTotalElements());
        assertEquals(roleEntities.getTotalPages(), result.getTotalPages());
    }

    @Test
    @Order(3)
    void givenPageRoleEntitiesNotEmptyWhenToPageDtoThenReturnRoleCreated() {
        List<RoleEntity> roleEntitiesList = new ArrayList<>();
        roleEntitiesList.add(roleEntity);
        Page<RoleEntity> roleEntities = new PageImpl<>(roleEntitiesList, PageRequest.of(0, 1), roleEntitiesList.size());

        PageResponse<RoleOutputDTO> result = mapper.toPageDto(roleEntities);
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(roleEntitiesList.get(0).getRoleExternalId(), result.getContent().get(0).roleExternalId());
        assertEquals(roleEntitiesList.get(0).getDescription(), result.getContent().get(0).description());
        assertEquals(roleEntitiesList.get(0).getAuthority(), result.getContent().get(0).authority());
        assertEquals(roleEntitiesList.get(0).getProject().getProjectExternalId(), result.getContent().get(0).project().projectExternalId());
        assertEquals(roleEntitiesList.get(0).getProject().getName(), result.getContent().get(0).project().name());
        assertEquals(roleEntitiesList.get(0).getProject().getCreatedAt(), result.getContent().get(0).project().createdAt());
        assertEquals(roleEntitiesList.get(0).getCreatedAt(), result.getContent().get(0).createdAt());
        assertEquals(roleEntities.getNumber(), result.getNumber());
        assertEquals(roleEntities.getSize(), result.getSize());
        assertEquals(roleEntities.getTotalElements(), result.getTotalElements());
        assertEquals(roleEntities.getTotalPages(), result.getTotalPages());
    }

}