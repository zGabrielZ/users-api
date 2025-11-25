package br.com.gabrielferreira.users.domain.specs;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.RoleFilter;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class RoleSpec {

    private static final String PROJECT = "project";

    private RoleSpec() {}

    public static Specification<RoleEntity> usingFilter(RoleFilter filter) {
        Specification<RoleEntity> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and();

        if (filter.isProjectExternalIdPresent()) {
            specification = specification.and(withProjectExternalId(filter.projectExternalId()));
        }

        if (filter.isRoleExternalIdPresent()) {
            specification = specification.and(withRoleExternalId(filter.roleExternalId()));
        }

        if (filter.isDescriptionNotBlank()) {
            specification = specification.and(withDescription(filter.description()));
        }

        if (filter.isAuthorityNotBlank()) {
            specification = specification.and(withAuthority(filter.authority()));
        }

        if (filter.isCreatedAtFromPresent()) {
            specification = specification.and(withCreatedAtFrom(filter.createdAtFrom()));
        }

        if (filter.isCreatedAtToPresent()) {
            specification = specification.and(withCreatedAtTo(filter.createdAtTo()));
        }

        return specification;
    }

    public static Specification<RoleEntity> withCreatedAtFrom(OffsetDateTime createAtFrom) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createAtFrom));
    }

    public static Specification<RoleEntity> withCreatedAtTo(OffsetDateTime  createdAtTo) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdAtTo));
    }

    public static Specification<RoleEntity> withAuthority(String authority) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("authority")), "%" + authority.toLowerCase() + "%"));
    }

    public static Specification<RoleEntity> withDescription(String description) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
    }

    public static Specification<RoleEntity> withRoleExternalId(UUID roleExternalId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("roleExternalId"), roleExternalId));
    }

    public static Specification<RoleEntity> withProjectExternalId(UUID projectExternalId) {
        return ((root, query, criteriaBuilder) -> {
            if (Objects.nonNull(query) && RoleEntity.class.equals(query.getResultType())) {
                root.fetch(PROJECT);
            } else {
                Join<RoleEntity, ProjectEntity> joinProject = root.join(PROJECT);
                joinProject.alias(PROJECT);
            }
            return criteriaBuilder.equal(root.get(PROJECT).get("projectExternalId"), projectExternalId);
        });
    }
}
