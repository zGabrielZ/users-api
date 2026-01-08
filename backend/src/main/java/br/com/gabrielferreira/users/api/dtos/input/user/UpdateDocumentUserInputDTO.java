package br.com.gabrielferreira.users.api.dtos.input.user;

import br.com.gabrielferreira.users.core.validations.document.ValidDocument;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@ValidDocument(number = "number", type = "type", property = "number")
public record UpdateDocumentUserInputDTO(
        @Schema(
                description = "CPF document number",
                example = "123.456.789-00"
        )
        @NotBlank
        String number
) implements Serializable {

    @JsonIgnore
    public DocumentType getType() {
        return DocumentType.CPF;
    }
}
