package br.com.gabrielferreira.users.api.dtos.input.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UpdateUserInputDTO(
        @Schema(
                description = "User first name",
                example = "John"
        )
        @NotBlank
        @Length(
                min = 1,
                max = 255
        )
        String firstName,

        @Schema(
                description = "User last name",
                example = "Doe"
        )
        @NotBlank
        @Length(
                min = 1,
                max = 255
        )
        String lastName
) implements Serializable {
}
