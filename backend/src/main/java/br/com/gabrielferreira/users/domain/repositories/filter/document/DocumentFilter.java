package br.com.gabrielferreira.users.domain.repositories.filter.document;

import br.com.gabrielferreira.users.domain.enums.DocumentType;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Builder
public record DocumentFilter(
        DocumentType type,
        String number
) implements Serializable {

    public boolean hasDocumentType() {
        return Objects.nonNull(this.type);
    }

    public boolean hasDocumentNumber() {
        return StringUtils.isNotBlank(this.number);
    }

    @Override
    public String number() {
        // Remove all non-digit characters from the document number to ensure consistent formatting for filtering
        return number.replaceAll("\\D", "");
    }
}
