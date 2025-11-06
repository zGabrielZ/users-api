package br.com.gabrielferreira.users.dtos.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProjectOutputDTO(
        @Schema(
                description = "Project external identifier",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID projectExternalId,

        @Schema(
                description = "Project name",
                example = "PROJECT_EVENT"
        )
        String name
) implements Serializable {
}
