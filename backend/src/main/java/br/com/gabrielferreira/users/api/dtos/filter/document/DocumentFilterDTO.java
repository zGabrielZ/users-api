package br.com.gabrielferreira.users.api.dtos.filter.document;

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
// TODO: Criar uma outra validação de acordo com o number, se o tipo for none, entao o number nao deve informar, se for outro tipo e o number for nulo, deve preencher o campo
@ValidDocument(number = "number", type = "type", property = "number")
public record DocumentFilterDTO(
        @Schema(
                description = """
                        Document type. <br/>
                        Allowed values are: CPF, CNPJ, NONE.
                        """,
                example = "CPF"
        )
        @NotNull
        DocumentType type,

        @Schema(
                description = """
                        Document number.<br/>
                        For CPF, the format should be 123.456.789-00 or 12345678900.<br/>
                        For CNPJ, the format should be 12.345.678/0001-00 or 12345678000100.<br/>
                        For CNPJ alphanumeric format should be 7P.BMR.TJ4/0001-17 or 7PBMRTJ4000117.<br/>
                        For NONE, this field should be null or empty.
                        """,
                example = "123.456.789-00"
        )
        @NotBlank
        String number
) implements Serializable {

}
