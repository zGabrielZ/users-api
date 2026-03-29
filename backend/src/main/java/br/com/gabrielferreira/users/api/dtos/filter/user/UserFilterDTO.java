package br.com.gabrielferreira.users.api.dtos.filter.user;

import br.com.gabrielferreira.users.api.dtos.filter.document.DocumentFilterDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserFilterDTO(
        @Schema(
                description = "User external identifier",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID userExternalId,

        @Schema(
                description = "User first name",
                example = "John"
        )
        String firstName,

        @Schema(
                description = "User last name",
                example = "Doe"
        )
        String lastName,

        @Schema(
                description = "User email",
                example = "john@email.com"
        )
        String email,

        @Schema(
                description = "Filter for users created from this date and time (ISO 8601 format)",
                example = "2024-01-01T00:00:00Z"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime createdAtFrom,

        @Schema(
                description = "Filter for users created up to this date and time (ISO 8601 format)",
                example = "2024-12-31T23:59:59Z"
        )
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        OffsetDateTime createdAtTo,

        @Schema(
                description = "User document information"
        )
        @Valid
        DocumentFilterDTO document
) implements Serializable {
}
