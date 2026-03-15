package br.com.gabrielferreira.users.api.mappers.role.input;

import br.com.gabrielferreira.users.api.dtos.filter.role.RoleFilterDTO;
import br.com.gabrielferreira.users.api.dtos.input.role.CreateRoleInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.role.UpdateRoleInputDTO;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.RoleFilter;
import br.com.gabrielferreira.users.stub.role.RoleFilterDTOStub;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleInputMapperTest {

    private final RoleInputMapper mapper = RoleInputMapper.INSTANCE;

    @Test
    @Order(1)
    void givenCreateRoleInputDtoWhenToEntityThenReturnRoleEntityCreated() {
        var createRoleInputDTO = CreateRoleInputDTO.builder()
                .description("Role Description")
                .authority("ROLE_USER")
                .build();

        RoleEntity result = mapper.toEntity(createRoleInputDTO);
        assertNotNull(result);
        assertEquals(createRoleInputDTO.description(), result.getDescription());
        assertEquals(createRoleInputDTO.authority(), result.getAuthority());
    }

    @Test
    @Order(2)
    void givenUpdateRoleInputDtoWhenToEntityThenReturnRoleEntityUpdated() {
        var updateRoleInputDTO = UpdateRoleInputDTO.builder()
                .description("Updated Role Description")
                .authority("ROLE_ADMIN")
                .build();

        RoleEntity result = mapper.toEntity(updateRoleInputDTO);
        assertNotNull(result);
        assertEquals(updateRoleInputDTO.description(), result.getDescription());
        assertEquals(updateRoleInputDTO.authority(), result.getAuthority());
    }

    @Test
    @Order(3)
    void givenRoleFilterDTOAndProjectExternalIdWhenToRoleFilterThenReturnRoleFilter() {
        RoleFilterDTO roleFilterDTO = RoleFilterDTOStub.createRoleFilterDTO();
        UUID projectExternalId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        RoleFilter result = mapper.toRoleFilter(roleFilterDTO, projectExternalId);
        assertNotNull(result);
        assertEquals(projectExternalId, result.projectExternalId());
        assertEquals(roleFilterDTO.roleExternalId(), result.roleExternalId());
        assertEquals(roleFilterDTO.description(), result.description());
        assertEquals(roleFilterDTO.authority(), result.authority());
        assertEquals(roleFilterDTO.createdAtFrom(), result.createdAtFrom());
        assertEquals(roleFilterDTO.createdAtTo(), result.createdAtTo());
    }
}