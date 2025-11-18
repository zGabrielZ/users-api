package br.com.gabrielferreira.users.domain.repositories.filter;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Builder
public record ProjectFilter(
        UUID projectExternalId,
        String name,
        OffsetDateTime createdAtFrom,
        OffsetDateTime createdAtTo
) implements Serializable {

    public boolean isProjectExternalIdPresent() {
        return Objects.nonNull(this.projectExternalId);
    }

    public boolean isNameNotBlank() {
        return StringUtils.isNotBlank(this.name);
    }

    public boolean isCreatedAtFromPresent() {
        return Objects.nonNull(this.createdAtFrom);
    }

    public boolean isCreatedAtToPresent() {
        return Objects.nonNull(this.createdAtTo);
    }
}
