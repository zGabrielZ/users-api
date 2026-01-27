package br.com.gabrielferreira.users.api.dtos.input.company;

import br.com.gabrielferreira.users.api.dtos.input.address.UpdateAddressInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.contact.UpdateContactInputDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UpdateCompanyInputDTO(
        @Schema(
                description = "Company name",
                example = "Acme Corporation"
        )
        @Length(
                min = 1,
                max = 255
        )
        String name,

        @Schema(
                description = "Company foundation date",
                example = "2000-01-15"
        )
        LocalDate foundationDate,

        @Schema(
                description = "Company website",
                example = "https://www.acme.com"
        )
        @Length(
                min = 1,
                max = 255
        )
        String site,

        @Schema(
                description = "Company email",
                example = "fake@email.com"
        )
        @Email
        String email,

        @Schema(
                description = "Company address"
        )
        @Valid
        UpdateAddressInputDTO address,

        @Schema(
                description = "Company contact"
        )
        @Valid
        UpdateContactInputDTO contact
) implements Serializable {
}
