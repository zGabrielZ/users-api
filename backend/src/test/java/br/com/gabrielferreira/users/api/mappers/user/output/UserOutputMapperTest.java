package br.com.gabrielferreira.users.api.mappers.user.output;

import br.com.gabrielferreira.users.api.dtos.output.page.PageResponse;
import br.com.gabrielferreira.users.api.dtos.output.user.UserOutputDTO;
import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.stub.document.DocumentEntityStub;
import br.com.gabrielferreira.users.stub.project.ProjectEntityStub;
import br.com.gabrielferreira.users.stub.user.UserEntityStub;
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

@DisplayName("Tests for UserOutputMapper")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserOutputMapperTest {

    private final UserOutputMapper mapper = UserOutputMapper.INSTANCE;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntityStub.userEntityCreated(
                ProjectEntityStub.createProjectEntity(null, null),
                "encodedPassword",
                DocumentEntityStub.documentCpfEntityCreated()
        );
    }

    @Test
    @Order(1)
    void givenUserEntityWhenToOutputDtoThenReturnUserOutputDTO() {
        UserOutputDTO result = mapper.toOutputDto(userEntity);
        assertNotNull(result);
        assertEquals(userEntity.getUserExternalId(), result.userExternalId());
        assertEquals(userEntity.getFirstName(), result.firstName());
        assertEquals(userEntity.getLastName(), result.lastName());
        assertEquals(userEntity.getEmail(), result.email());
        assertEquals(userEntity.getCreatedAt(), result.createdAt());
        assertNotNull(result.document());
        assertEquals(userEntity.getDocument().getDocumentExternalId(), result.document().documentExternalId());
        assertEquals(userEntity.getDocument().getType(), result.document().type());
        assertEquals(Mask.formatDocument(userEntity.getDocument().getType(), userEntity.getDocument().getNumber()), result.document().number());
    }

    @Test
    @Order(2)
    void givenUserEntitiesEmptyWhenToPageThenReturnUsersPageResponse() {
        List<UserEntity> userEntityList = new ArrayList<>();
        Page<UserEntity> userEntities = new PageImpl<>(userEntityList, PageRequest.of(0, 1), 0);

        PageResponse<UserOutputDTO> result = mapper.toPageDto(userEntities);
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(userEntities.getNumber(), result.getNumber());
        assertEquals(userEntities.getSize(), result.getSize());
        assertEquals(userEntities.getTotalElements(), result.getTotalElements());
        assertEquals(userEntities.getTotalPages(), result.getTotalPages());
    }

    @Test
    @Order(3)
    void givenUserEntitiesNotEmptyWhenToPageThenReturnUsersPageResponse() {
        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(userEntity);
        Page<UserEntity> userEntities = new PageImpl<>(userEntityList, PageRequest.of(0, 1), 1);

        PageResponse<UserOutputDTO> result = mapper.toPageDto(userEntities);
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(userEntities.getNumber(), result.getNumber());
        assertEquals(userEntities.getSize(), result.getSize());
        assertEquals(userEntities.getTotalElements(), result.getTotalElements());
        assertEquals(userEntities.getTotalPages(), result.getTotalPages());
    }
}