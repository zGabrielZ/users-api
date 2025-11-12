package br.com.gabrielferreira.users.api.dtos.input.role;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UpdateRoleInputDTO(
        @Schema(
                description = "Role name",
                example = "Administrator"
        )
        @NotBlank
        @Length(
                min = 1,
                max = 255
        )
        String description,

        @Schema(
                description = "Role authority",
                example = "ROLE_ADMINISTRATOR"
        )
        @NotBlank
        @Length(
                min = 1,
                max = 255
        )
        String authority
) implements Serializable {
}
