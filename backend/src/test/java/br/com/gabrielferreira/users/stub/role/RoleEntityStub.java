package br.com.gabrielferreira.users.stub.role;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.stub.project.ProjectEntityStub;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RoleEntityStub {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());
    }

    private static final OffsetDateTime FIXED_DATE = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final UUID FIXED_UUID = UUID.fromString("da4f3715-c033-486e-af4c-d9e30e5b4158");

    private RoleEntityStub() {}

    public static RoleEntity createRoleEntity(
            String description,
            String authority,
            UUID roleExternalId,
            ProjectEntity projectEntity
    ) {
        return RoleEntity.builder()
                .id(1L)
                .description(StringUtils.isBlank(description) ? "Default Role Name" : description)
                .authority(StringUtils.isBlank(authority) ? "Default Authority Name" : authority)
                .roleExternalId(Objects.isNull(roleExternalId) ? FIXED_UUID : roleExternalId)
                .project(projectEntity)
                .createdAt(FIXED_DATE)
                .build();
    }

    public static Page<RoleEntity> createPageOfRoleEntities(Pageable pageable) {
        RoleEntity roleEntity1 = createRoleEntity(
                "Admin",
                "ROLE_ADMIN",
                UUID.fromString("dddc9953-0cd4-4525-8914-37219bc43552"),
                ProjectEntityStub.createProjectEntity("Project ABC", UUID.fromString("3b5f4607-0b85-4673-b579-a4b67d92746b"))
        );
        RoleEntity roleEntity2 = createRoleEntity(
                "Client",
                "ROLE_CLIENT",
                UUID.fromString("cdd53b28-da77-4d0f-9da3-3f2a6f826f6b"),
                ProjectEntityStub.createProjectEntity("Project DEF", UUID.fromString("ee493bc1-2b40-49c3-8816-02f03eddea41"))
                );
        List<RoleEntity> roles = List.of(roleEntity1, roleEntity2);
        return new PageImpl<>(roles, pageable, roles.size());
    }
}
