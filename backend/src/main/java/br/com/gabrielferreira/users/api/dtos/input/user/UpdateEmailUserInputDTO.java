package br.com.gabrielferreira.users.api.dtos.input.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
        // The regex pattern is added to ensure that the email follows a valid format, including a domain and a top-level domain.
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "{user.email.invalid}")
        String email
) implements Serializable {
}
