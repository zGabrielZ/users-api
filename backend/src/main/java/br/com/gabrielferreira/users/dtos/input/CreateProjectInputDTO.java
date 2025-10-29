package br.com.gabrielferreira.users.dtos.input;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Builder
public record CreateProjectInputDTO(
        @NotBlank
        @Length(min = 1, max = 255)
        String name
) implements Serializable {
}
