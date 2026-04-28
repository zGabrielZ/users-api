package br.com.gabrielferreira.users.api.dtos.filter.document;

import br.com.gabrielferreira.users.core.validations.document.ValidDocument;
import br.com.gabrielferreira.users.core.validations.document.type.ValidDocumentType;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ValidDocument(number = "number", type = "documentType", property = "number")
public record DocumentFilterDTO(
        @Schema(
                description = """
                        Document type. <br/>
                        Allowed values are: CPF, CNPJ, NONE.
                        """,
                example = "CPF"
        )
        @NotNull
        @ValidDocumentType
        String type,

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

        @JsonIgnore
        public DocumentType getDocumentType() {
                if (StringUtils.isNotBlank(this.type)) {
                        return DocumentType.valueOf(type);
                }
                return null;
        }
}
