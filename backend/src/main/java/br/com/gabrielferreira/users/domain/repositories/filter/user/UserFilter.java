package br.com.gabrielferreira.users.domain.repositories.filter.user;

import br.com.gabrielferreira.users.domain.repositories.filter.document.DocumentFilter;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Builder
public record UserFilter(
        UUID userExternalId,
        String firstName,
        String lastName,
        String email,
        OffsetDateTime createdAtFrom,
        OffsetDateTime createdAtTo,
        DocumentFilter document
) implements Serializable {

    public boolean hasUserExternalId() {
        return Objects.nonNull(this.userExternalId);
    }

    public boolean hasFirstName() {
        return StringUtils.isNotBlank(this.firstName);
    }

    public boolean hasLastName() {
        return StringUtils.isNotBlank(this.lastName);
    }

    public boolean hasEmail() {
        return StringUtils.isNotBlank(this.email);
    }

    public boolean hasCreatedAtFrom() {
        return Objects.nonNull(this.createdAtFrom);
    }

    public boolean hasCreatedAtTo() {
        return Objects.nonNull(this.createdAtTo);
    }

    public boolean hasDocument() {
        return Objects.nonNull(this.document);
    }
}
