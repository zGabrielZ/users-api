package br.com.gabrielferreira.users.api.dtos.input.document;

import br.com.gabrielferreira.users.core.validations.document.ValidDocument;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ValidDocument(number = "number", type = "type", property = "")
public record DocumentInputDTO(
        // TODO: colocar outro document type que no momento aceita o CNPJ e CPF
        // TODO: criar outros endpoints que aceita CNPJ para empresas
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

}
