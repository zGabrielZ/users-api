package br.com.gabrielferreira.users.stub.role;

import br.com.gabrielferreira.users.api.dtos.filter.role.RoleFilterDTO;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RoleFilterDTOStub {

    private static final OffsetDateTime FIXED_DATE = OffsetDateTime.parse("2026-01-01T10:00:00Z");
    private static final OffsetDateTime FIXED_DATE_2 = OffsetDateTime.parse("2026-01-02T10:00:00Z");
    private static final UUID FIXED_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private RoleFilterDTOStub() {}

    public static RoleFilterDTO createRoleFilterDTO() {
        return RoleFilterDTO.builder()
                .roleExternalId(FIXED_UUID)
                .description("Role Description")
                .authority("ROLE_USER")
                .createdAtFrom(FIXED_DATE)
                .createdAtTo(FIXED_DATE_2)
                .build();
    }
}
