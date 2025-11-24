package br.com.gabrielferreira.users.api.dtos.output.role;

import br.com.gabrielferreira.users.api.dtos.output.project.ProjectOutputDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record RoleOutputDTO(
        @Schema(
                description = "Role external identifier",
                example = "8f6ce0d6-dd45-4e89-b165-2d147ff58051"
        )
        UUID roleExternalId,

        @Schema(
                description = "Role name",
                example = "Administrator"
        )
        String description,

        @Schema(
                description = "Role authority",
                example = "ROLE_ADMINISTRATOR"
        )
        String authority,

        @Schema(
                description = "Role creation date and time",
                example = "2024-06-01T12:00:00Z"
        )
        OffsetDateTime createdAt,

        @Schema(
                description = "Project associated with the role"
        )
        ProjectOutputDTO project
) implements Serializable {
}
