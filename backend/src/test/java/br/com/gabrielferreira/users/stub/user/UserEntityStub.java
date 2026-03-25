package br.com.gabrielferreira.users.stub.user;

import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.utils.GenerateCPFUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class UserEntityStub {

    private static final OffsetDateTime FIXED_DATE = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final UUID FIXED_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private UserEntityStub() {}

    public static UserEntity createUserEntity(ProjectEntity projectEntity) {
        return UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .password("password")
                .project(projectEntity)
                .document(
                        DocumentEntity.builder()
                                .type(DocumentType.CPF)
                                .number(GenerateCPFUtils.generateCPF())
                                .build()
                )
                .build();
    }

    public static UserEntity userEntityCreated(ProjectEntity projectEntity, String encodedPassword, DocumentEntity documentEntity) {
        return UserEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .password(encodedPassword)
                .project(projectEntity)
                .userExternalId(FIXED_UUID)
                .roles(new ArrayList<>())
                .companyUsers(new ArrayList<>())
                .document(documentEntity)
                .createdAt(FIXED_DATE)
                .build();
    }
}
