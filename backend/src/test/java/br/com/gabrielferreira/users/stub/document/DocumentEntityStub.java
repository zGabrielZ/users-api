package br.com.gabrielferreira.users.stub.document;

import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import br.com.gabrielferreira.users.utils.GenerateCPFUtils;

import java.util.UUID;

public class DocumentEntityStub {

    private static final UUID FIXED_UUID = UUID.fromString("d62bf3d6-d7ae-4b45-96cc-ff438dd0263f");

    private DocumentEntityStub() {}

    public static DocumentEntity documentCpfEntityCreated() {
        return DocumentEntity.builder()
                .id(1L)
                .documentExternalId(FIXED_UUID)
                .type(DocumentType.CPF)
                .number(GenerateCPFUtils.generateCPF())
                .build();
    }

    public static DocumentEntity documentNoneEntityCreated() {
        return DocumentEntity.builder()
                .id(2L)
                .documentExternalId(UUID.randomUUID())
                .type(DocumentType.NONE)
                .build();
    }
}
