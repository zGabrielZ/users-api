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
public record ProjectFilterDTO(
        @Schema(
                description = "External ID of the project",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID projectExternalId,

        @Schema(
                description = "Name of the project",
                example = "New Project"
        )
        String name,

        @Schema(
                description = "Filter for projects created from this date and time (ISO 8601 format)",
                example = "2024-01-01T00:00:00Z"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime createdAtFrom,

        @Schema(
                description = "Filter for projects created up to this date and time (ISO 8601 format)",
                example = "2024-12-31T23:59:59Z"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime createdAtTo
) implements Serializable {

}
