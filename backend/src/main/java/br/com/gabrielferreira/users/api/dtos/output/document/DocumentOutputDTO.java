package br.com.gabrielferreira.users.api.dtos.output.document;

import br.com.gabrielferreira.users.core.utils.Mask;
import br.com.gabrielferreira.users.domain.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DocumentOutputDTO(
        @Schema(
                description = "Document external identifier",
                example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        )
        UUID documentExternalId,

        @Schema(
                description = "Document type",
                example = "CPF"
        )
        DocumentType type,

        @Schema(
                description = "Document number",
                example = "123.456.789-00"
        )
        String number
) implements Serializable {

        @Override
        public String number() {
                return Mask.formatDocument(this.type, this.number);
        }
}
