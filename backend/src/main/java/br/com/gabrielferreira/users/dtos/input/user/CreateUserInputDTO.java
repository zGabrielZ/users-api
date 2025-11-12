package br.com.gabrielferreira.users.dtos.input.user;

import br.com.gabrielferreira.users.dtos.input.document.DocumentInputDTO;
import br.com.gabrielferreira.users.validations.password.ValidPassword;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CreateUserInputDTO(
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
        String lastName,

        @Schema(
                description = "User email",
                example = "john@email.com"
        )
        @NotBlank
        @Email
        String email,

        @Schema(
                description = "User password",
                example = "strongPassword123"
        )
        @NotBlank
        @Length(
                min = 1,
                max = 255
        )
        @ValidPassword
        String password,

        @Schema(
                description = "User document information"
        )
        @Valid
        DocumentInputDTO document
) implements Serializable {
}
