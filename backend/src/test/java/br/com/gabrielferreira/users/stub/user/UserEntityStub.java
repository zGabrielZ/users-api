package br.com.gabrielferreira.users.stub.user;

import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.utils.GenerateCPFUtils;

public class UserEntityStub {

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
}
