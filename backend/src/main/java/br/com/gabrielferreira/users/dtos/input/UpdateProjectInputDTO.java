package br.com.gabrielferreira.users.dtos.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Builder
public record UpdateProjectInputDTO(
        @Schema(
                description = "Name of the project",
                example = "PROJECT_BOOKSTORE"
        )
        @NotBlank
        @Length(
                min = 1,
                max = 255
        )
        String name
) implements Serializable {
}
