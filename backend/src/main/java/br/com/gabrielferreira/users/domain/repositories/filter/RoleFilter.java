package br.com.gabrielferreira.users.domain.repositories.filter;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Builder
public record RoleFilter(
        UUID roleExternalId,
        String description,
        String authority,
        OffsetDateTime createdAtFrom,
        OffsetDateTime createdAtTo,
        UUID projectExternalId
) implements Serializable {

    public boolean isRoleExternalIdPresent() {
        return Objects.nonNull(this.roleExternalId);
    }

    public boolean isDescriptionNotBlank() {
        return StringUtils.isNotBlank(this.description);
    }

    public boolean isAuthorityNotBlank() {
        return StringUtils.isNotBlank(this.authority);
    }

    public boolean isCreatedAtFromPresent() {
        return Objects.nonNull(this.createdAtFrom);
    }

    public boolean isCreatedAtToPresent() {
        return Objects.nonNull(this.createdAtTo);
    }

    public boolean isProjectExternalIdPresent() {
        return Objects.nonNull(this.projectExternalId);
    }
}
