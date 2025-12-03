package br.com.gabrielferreira.users.domain.specs;

import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.RoleEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.RoleFilter;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class UserRoleSpec implements Specification<RoleEntity> {

    @Serial
    private static final long serialVersionUID = -1549798442742862620L;

    private final UUID userExternalId;

    private final RoleFilter filter;

    @Override
    public Predicate toPredicate(Root<RoleEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Join<RoleEntity, UserEntity> userJoin = root.join("users", JoinType.INNER);
        predicates.add(criteriaBuilder.equal(userJoin.get("userExternalId"), userExternalId));

        if (filter.isProjectExternalIdPresent()) {
            Join<RoleEntity, ProjectEntity> projectJoin = root.join("project", JoinType.INNER);
            Predicate predicateProjectExternalId = criteriaBuilder.equal(
                    projectJoin.get("projectExternalId"), filter.projectExternalId()
            );
            predicates.add(predicateProjectExternalId);
        }

        if (filter.isRoleExternalIdPresent()) {
            Predicate predicateRoleExternalId = criteriaBuilder.equal(
                    root.get("roleExternalId"), filter.roleExternalId()
            );
            predicates.add(predicateRoleExternalId);
        }

        if (filter.isDescriptionNotBlank()) {
            Predicate predicateDescription = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), "%" + filter.description().toLowerCase() + "%"
            );
            predicates.add(predicateDescription);
        }

        if (filter.isAuthorityNotBlank()) {
            Predicate predicateAuthority = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("authority")), "%" + filter.authority().toLowerCase() + "%"
            );
            predicates.add(predicateAuthority);
        }

        if (filter.isCreatedAtFromPresent()) {
            Predicate predicateCreatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"), filter.createdAtFrom()
            );
            predicates.add(predicateCreatedAtFrom);
        }

        if (filter.isCreatedAtToPresent()) {
            Predicate predicateCreatedAtTo = criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"), filter.createdAtTo()
            );
            predicates.add(predicateCreatedAtTo);
        }

        if (Objects.nonNull(query) && RoleEntity.class.equals(query.getResultType())) {
            root.fetch("users", JoinType.INNER);
            root.fetch("project", JoinType.INNER);
        } else {
            root.join("users", JoinType.INNER);
            root.join("project", JoinType.INNER);
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
