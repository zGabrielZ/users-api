package br.com.gabrielferreira.users.domain.specs;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.ProjectFilter;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ProjectSpec {

    private ProjectSpec() {}

    public static Specification<ProjectEntity> usingFilter(ProjectFilter filter) {
        Specification<ProjectEntity> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and();

        if (filter.isProjectExternalIdPresent()) {
            specification = specification.and(withProjectExternalId(filter.projectExternalId()));
        }

        if (filter.isNameNotBlank()) {
            specification = specification.and(withName(filter.name()));
        }

        if (filter.isCreatedAtFromPresent()) {
            specification = specification.and(withCreatedAtFrom(filter.createdAtFrom()));
        }

        if (filter.isCreatedAtToPresent()) {
            specification = specification.and(withCreatedAtTo(filter.createdAtTo()));
        }

        return specification;
    }

    public static Specification<ProjectEntity> withProjectExternalId(UUID projectExternalId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("projectExternalId"), projectExternalId));
    }

    public static Specification<ProjectEntity> withName(String name) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    public static Specification<ProjectEntity> withCreatedAtFrom(OffsetDateTime createAtFrom) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), createAtFrom));
    }

    public static Specification<ProjectEntity> withCreatedAtTo(OffsetDateTime  createdAtTo) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), createdAtTo));
    }
}
