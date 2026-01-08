package br.com.gabrielferreira.users.api.dtos.input.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressInputDTO(
        @Schema(
                description = "Postal code",
                example = "03342-900"
        )
        @NotBlank(message = "{address.postalCode}")
        String postalCode,

        @Schema(
                description = "Street name",
                example = "Avenida Regente Feijó"
        )
        @NotBlank(message = "{address.street}")
        String street,

        @Schema(
                description = "House or building number",
                example = "123"
        )
        @NotBlank(message = "{address.number}")
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
        @NotBlank(message = "{address.neighborhood}")
        String neighborhood,

        @Schema(
                description = "City name",
                example = "São Paulo"
        )
        @NotBlank(message = "{address.city}")
        String city,

        @Schema(
                description = "State abbreviation",
                example = "SP"
        )
        @NotBlank(message = "{address.state}")
        String state
) implements Serializable {
}
