package br.com.gabrielferreira.users.api.dtos.input.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UpdateAddressInputDTO(
        @Schema(
                description = "Postal code",
                example = "03342-900"
        )
        String postalCode,

        @Schema(
                description = "Street name",
                example = "Avenida Regente Feijó"
        )
        String street,

        @Schema(
                description = "House or building number",
                example = "123"
        )
        String number,

        @Schema(
                description = "Address complement",
                example = "Apt 202"
        )
        String complement,

        @Schema(
                description = "Neighborhood name",
                example = "Vila Regente Feijó"
        )
        String neighborhood,

        @Schema(
                description = "City name",
                example = "São Paulo"
        )
        String city,

        @Schema(
                description = "State abbreviation",
                example = "SP"
        )
        String state
) implements Serializable {
}
