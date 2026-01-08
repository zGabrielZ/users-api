package br.com.gabrielferreira.users.api.dtos.output.company;

import br.com.gabrielferreira.users.api.dtos.output.address.AddressOutputDTO;
import br.com.gabrielferreira.users.api.dtos.output.contact.ContactOutputDTO;
import br.com.gabrielferreira.users.api.dtos.output.document.DocumentOutputDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record CompanyOutputDTO(
        @Schema(
                description = "Company external ID",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID companyExternalId,

        @Schema(
                description = "Company name",
                example = "Acme Corporation"
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
        String site,

        @Schema(
                description = "Company email",
                example = "fake@email.com"
        )
        String email,

        @Schema(
                description = "Company address"
        )
        AddressOutputDTO address,

        @Schema(
                description = "Company contact"
        )
        ContactOutputDTO contact,

        @Schema(
                description = "Company creation date",
                example = "2024-06-01T12:00:00Z"
        )
        OffsetDateTime createdAt,

        @Schema(
               description = "Company document",
                example = """
                        {
                          "documentExternalId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                          "type": "CNPJ",
                          "number": "12.345.678/0001-90"
                        }
                        """
        )
        DocumentOutputDTO document
) implements Serializable {

}
