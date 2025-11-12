package br.com.gabrielferreira.users.dtos.input.document;

import br.com.gabrielferreira.users.enums.DocumentType;
import br.com.gabrielferreira.users.validations.document.ValidDocument;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ValidDocument(number = "number", type = "type")
public record DocumentInputDTO(
        @Schema(
                description = "Document type",
                example = "CPF"
        )
        @NotNull
        DocumentType type,

        @Schema(
                description = "Document number",
                example = "123.456.789-00"
        )
        @NotBlank
        String number
) implements Serializable {

        @Override
        public String number() {
                return switch (type) {
                        case CNPJ -> number.replaceAll("\\W", "");
                        case CPF -> number.replaceAll("\\D", "");
                        default -> number;
                };
        }
}
