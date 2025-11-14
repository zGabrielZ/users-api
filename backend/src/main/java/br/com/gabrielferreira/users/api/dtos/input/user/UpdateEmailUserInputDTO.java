package br.com.gabrielferreira.users.api.dtos.input.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UpdateEmailUserInputDTO(
        @Schema(
                description = "User email",
                example = "john@email.com"
        )
        @NotBlank
        @Email
        String email
) implements Serializable {
}
