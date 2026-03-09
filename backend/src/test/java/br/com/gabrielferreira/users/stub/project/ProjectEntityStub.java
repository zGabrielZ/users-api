package br.com.gabrielferreira.users.stub.project;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProjectEntityStub {

    private static final OffsetDateTime FIXED_DATE = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final UUID FIXED_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private ProjectEntityStub() {}

    public static ProjectEntity createProjectEntity(String name, UUID projectExternalId) {
        return ProjectEntity.builder()
                .id(1L)
                .name(StringUtils.isBlank(name) ? "Default Project Name" : name)
                .projectExternalId(Objects.isNull(projectExternalId) ? FIXED_UUID : projectExternalId)
                .createdAt(FIXED_DATE)
                .build();
    }

    public static Page<ProjectEntity> createPageOfProjectEntities(Pageable pageable) {
        ProjectEntity project1 = createProjectEntity("Project A", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        ProjectEntity project2 = createProjectEntity("Project B", UUID.fromString("223e4567-e89b-12d3-a456-426614174001"));
        List<ProjectEntity> projects = List.of(project1, project2);
        return new PageImpl<>(projects, pageable, projects.size());
    }
}
