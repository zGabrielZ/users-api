package br.com.gabrielferreira.users.stub.project;

import br.com.gabrielferreira.users.api.dtos.filter.project.ProjectFilterDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ProjectFilterDTOStub {

    private static final OffsetDateTime FIXED_DATE = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final OffsetDateTime FIXED_DATE_2 = OffsetDateTime.parse("2026-01-02T10:00:00Z");
    private static final UUID FIXED_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private ProjectFilterDTOStub() {}

    public static ProjectFilterDTO createProjectFilterDto() {
        return ProjectFilterDTO.builder()
                .projectExternalId(FIXED_UUID)
                .name("Project Filter")
                .createdAtTo(FIXED_DATE)
                .createdAtFrom(FIXED_DATE_2)
                .build();
    }
}
