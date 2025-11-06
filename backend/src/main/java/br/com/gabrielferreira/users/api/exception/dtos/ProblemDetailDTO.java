package br.com.gabrielferreira.users.api.exception.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProblemDetailDTO(
        @Schema(
                description = "HTTP status code associated with the problem",
                example = "400"
        )
        Integer status,
        @Schema(
                description = "URI identifying the type of problem",
                example = "https://example.com/probs/invalid-data"
        )
        String type,
        @Schema(
                description = "Short, human-readable summary of the problem type",
                example = "Invalid Data"
        )
        String title,
        @Schema(
                description = "Detailed explanation of the problem",
                example = "One or more fields are invalid. Please correct and try again."
        )
        String detail,
        @Schema(
                description = "User-friendly message about the problem",
                example = "The provided data is invalid. Please check the fields and try again."
        )
        String message,
        @Schema(
                description = "Timestamp when the problem occurred",
                example = "2024-06-01T12:34:56Z"
        )
        OffsetDateTime timestamp,
        @Schema(
                description = "List of fields that caused the problem"
        )
        List<ProblemDetailFieldDTO> fields
) implements Serializable {

}
