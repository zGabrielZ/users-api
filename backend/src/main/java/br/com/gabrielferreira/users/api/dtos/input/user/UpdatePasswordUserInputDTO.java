package br.com.gabrielferreira.users.api.dtos.input.user;

import br.com.gabrielferreira.users.core.validations.password.ValidPassword;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UpdatePasswordUserInputDTO(
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
        String newPassword,

        @Schema(
                description = "User old password",
                example = "oldPassword123"
        )
        @NotBlank
        @Length(
                min = 1,
                max = 255
        )
        String oldPassword
) implements Serializable {
}
