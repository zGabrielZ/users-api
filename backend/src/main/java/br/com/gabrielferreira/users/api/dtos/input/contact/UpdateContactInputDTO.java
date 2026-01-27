package br.com.gabrielferreira.users.api.dtos.input.contact;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UpdateContactInputDTO(
        @Schema(
                description = "Contact name",
                example = "John Doe"
        )
        String name,

        @Schema(
                description = "Contact phone DDI",
                example = "55"
        )
        String ddiPhone,

        @Schema(
                description = "Contact phone DDD",
                example = "11"
        )
        String dddPhone,

        @Schema(
                description = "Contact phone number",
                example = "91234-5678"
        )
        String phoneNumber,

        @Schema(
                description = "Contact mobile DDI",
                example = "55"
        )
        String ddiMobile,

        @Schema(
                description = "Contact mobile DDD",
                example = "11"
        )
        String dddMobile,

        @Schema(
                description = "Contact mobile number",
                example = "99876-5432"
        )
        String mobileNumber
) implements Serializable {
}
