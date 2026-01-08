package br.com.gabrielferreira.users.api.dtos.output.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressOutputDTO(
        @Schema(
                description = "Postal code",
                example = "12345-678"
        )
        String postalCode,

        @Schema(
                description = "Street name",
                example = "Main St"
        )
        String street,

        @Schema(
                description = "House or building number",
                example = "100"
        )
        String number,

        @Schema(
                description = "Address complement",
                example = "Apt 202"
        )
        String complement,

        @Schema(
                description = "Neighborhood name",
                example = "Downtown"
        )
        String neighborhood,

        @Schema(
                description = "City name",
                example = "Springfield"
        )
        String city,

        @Schema(
                description = "State abbreviation",
                example = "IL"
        )
        String state
) implements Serializable {
}
