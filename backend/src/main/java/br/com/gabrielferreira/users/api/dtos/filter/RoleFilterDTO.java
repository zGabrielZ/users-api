package br.com.gabrielferreira.users.api.dtos.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record RoleFilterDTO(
        @Schema(
                description = "External ID of the role",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID roleExternalId,

        @Schema(
                description = "Description of the role",
                example = "Administrator role with full permissions"
        )
        String description,

        @Schema(
                description = "Authority of the role",
                example = "ROLE_ADMIN"
        )
        String authority,

        @Schema(
                description = "Filter for roles created from this date and time (ISO 8601 format)",
                example = "2024-01-01T00:00:00Z"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime createdAtFrom,

        @Schema(
                description = "Filter for roles created up to this date and time (ISO 8601 format)",
                example = "2024-12-31T23:59:59Z"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime createdAtTo
) implements Serializable {

}
