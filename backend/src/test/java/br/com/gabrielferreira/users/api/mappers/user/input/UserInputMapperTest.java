package br.com.gabrielferreira.users.api.mappers.user.input;

import br.com.gabrielferreira.users.api.dtos.filter.document.DocumentFilterDTO;
import br.com.gabrielferreira.users.api.dtos.filter.user.UserFilterDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.CreateUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdateDocumentUserInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.user.UpdateUserInputDTO;
import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.domain.repositories.filter.document.DocumentFilter;
import br.com.gabrielferreira.users.domain.repositories.filter.user.UserFilter;
import br.com.gabrielferreira.users.utils.GenerateCPFUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Tests for UserInputMapperTest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserInputMapperTest {

    private final UserInputMapper mapper = UserInputMapper.INSTANCE;

    @Test
    @Order(1)
    void givenCreateUserInputWhenToEntityThenReturnUserEntityCreated() {
        CreateUserInputDTO createUserInputDTO = CreateUserInputDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@email.com")
                .password("strongPassword123")
                .build();

        UserEntity result = mapper.toEntity(createUserInputDTO);
        assertNotNull(result);
        assertEquals(createUserInputDTO.firstName(), result.getFirstName());
        assertEquals(createUserInputDTO.lastName(), result.getLastName());
        assertEquals(createUserInputDTO.email(), result.getEmail());
        assertEquals(createUserInputDTO.password(), result.getPassword());
    }

    @Test
    @Order(2)
    void givenUpdateUserInputWhenToEntityThenReturnUserEntityUpdated() {
        UpdateUserInputDTO updateUserInputDTO = UpdateUserInputDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .build();

        UserEntity result = mapper.toEntity(updateUserInputDTO);
        assertNotNull(result);
        assertEquals(updateUserInputDTO.firstName(), result.getFirstName());
        assertEquals(updateUserInputDTO.lastName(), result.getLastName());
    }

    @Test
    @Order(3)
    void givenUpdateDocumentUserInputWhenToEntityThenReturnDocumentEntityUpdated() {
        UpdateDocumentUserInputDTO updateDocumentUserInputDTO = UpdateDocumentUserInputDTO.builder()
                .number(GenerateCPFUtils.generateCPF())
                .build();

        DocumentEntity result = mapper.toEntity(updateDocumentUserInputDTO);
        assertNotNull(result);
        assertEquals(updateDocumentUserInputDTO.number(), result.getNumber());
        assertEquals(DocumentType.CPF, result.getType());
    }

    @Test
    @Order(4)
    void givenUserFilterWhenToUserFilterThenReturnUserFilterCreated() {
        UserFilterDTO userFilterDTO = UserFilterDTO.builder()
                .userExternalId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("test@email.com")
                .createdAtFrom(OffsetDateTime.now())
                .createdAtTo(OffsetDateTime.now().plusDays(1))
                .document(
                        DocumentFilterDTO.builder()
                                .type("CPF")
                                .number(GenerateCPFUtils.generateCPF())
                                .build()
                )
                .build();

        UserFilter result = mapper.toUserFilter(userFilterDTO);
        assertNotNull(result);
        assertEquals(userFilterDTO.userExternalId(), result.userExternalId());
        assertEquals(userFilterDTO.firstName(), result.firstName());
        assertEquals(userFilterDTO.lastName(), result.lastName());
        assertEquals(userFilterDTO.email(), result.email());
        assertEquals(userFilterDTO.createdAtFrom(), result.createdAtFrom());
        assertEquals(userFilterDTO.createdAtTo(), result.createdAtTo());
        assertNotNull(result.document());
        assertEquals(userFilterDTO.document().type(), result.document().type().name());
        assertEquals(userFilterDTO.document().number(), result.document().number());
    }

    @Test
    @Order(5)
    void givenDocumentFilterWhenToDocumentFilterThenReturnDocumentFilterCreated() {
        DocumentFilterDTO documentFilterDTO = DocumentFilterDTO.builder()
                .type("CPF")
                .number(GenerateCPFUtils.generateCPF())
                .build();

        DocumentFilter result = mapper.mapDocumentFilter(documentFilterDTO);
        assertNotNull(result);
        assertEquals(documentFilterDTO.type(), result.type().name());
        assertEquals(documentFilterDTO.number(), result.number());
    }
}