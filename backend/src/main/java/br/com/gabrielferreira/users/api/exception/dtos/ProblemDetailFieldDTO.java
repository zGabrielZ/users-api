package br.com.gabrielferreira.users.api.exception.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProblemDetailFieldDTO(
        @Schema(
                description = "Name of the field that caused the problem",
                example = "email"
        )
        String field,
        @Schema(
                description = "Detailed message about the problem with the field",
                example = "The email format is invalid."
        )
        String message
) implements Serializable {
}
