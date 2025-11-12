package br.com.gabrielferreira.users.api.dtos.output.user;

import br.com.gabrielferreira.users.api.dtos.output.document.DocumentOutputDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserOutputDTO(
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
                description = "User document information"
        )
        DocumentOutputDTO document
) implements Serializable {
}
