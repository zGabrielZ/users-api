package br.com.gabrielferreira.users.stub;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ProjectEntityStub {

    private ProjectEntityStub() {}

    private static final UUID FIXED_UUID = UUID.fromString("8a6e1ad2-57f5-4ea5-ae0e-c3424a404caf");

    private static final OffsetDateTime FIXED_DATE = OffsetDateTime.parse("2026-01-01T10:00:00Z");

    public static ProjectEntity createProjectEntity(String name) {
        return ProjectEntity.builder()
                .name(name)
                .build();
    }

    public static ProjectEntity createProjectEntityWithMoreInfo(String name) {
        return ProjectEntity.builder()
                .id(1L)
                .name(name)
                .projectExternalId(FIXED_UUID)
                .createdAt(FIXED_DATE)
                .build();
    }
}
