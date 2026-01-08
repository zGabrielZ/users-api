package br.com.gabrielferreira.users.api.dtos.input.company;

import br.com.gabrielferreira.users.api.dtos.input.address.AddressInputDTO;
import br.com.gabrielferreira.users.api.dtos.input.contact.ContactInputDTO;
import br.com.gabrielferreira.users.core.validations.document.ValidDocument;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ValidDocument(number = "documentNumber", type = "type", property = "documentNumber")
public record CreateCompanyInputDTO(
        @Schema(
                description = "Company name",
                example = "Acme Corporation"
        )
        @NotBlank
        @Length(
                min = 1,
                max = 255
        )
        String name,

        @Schema(
                description = "Company CNPJ number",
                example = "12.345.678/0001-90"
        )
        @NotBlank
        String documentNumber,

        @Schema(
                description = "Company foundation date",
                example = "2000-01-15"
        )
        @NotNull
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
        @NotBlank
        @Email
        String email,

        @Schema(
                description = "Company address"
        )
        @NotNull
        @Valid
        AddressInputDTO address,

        @Schema(
                description = "Company contact"
        )
        @NotNull
        @Valid
        ContactInputDTO contact
) implements Serializable {

    @JsonIgnore
    public DocumentType getType() {
        return DocumentType.CNPJ;
    }
}
