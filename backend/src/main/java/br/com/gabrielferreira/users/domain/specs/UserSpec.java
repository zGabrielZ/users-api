package br.com.gabrielferreira.users.domain.specs;

import br.com.gabrielferreira.users.domain.entities.DocumentEntity;
import br.com.gabrielferreira.users.domain.entities.ProjectEntity;
import br.com.gabrielferreira.users.domain.entities.UserEntity;
import br.com.gabrielferreira.users.domain.repositories.filter.document.DocumentFilter;
import br.com.gabrielferreira.users.domain.repositories.filter.user.UserFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class UserSpec implements Specification<UserEntity> {

    private static final String PROJECT = "project";
    private static final String DOCUMENT = "document";

    @Serial
    private static final long serialVersionUID = 3480545389646643043L;

    private final UUID projectExternalId;

    private final UserFilter userFilter;

    @Override
    public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Join<UserEntity, ProjectEntity> projectJoin = root.join(PROJECT, JoinType.INNER);
        Predicate predicateProjectExternalId = criteriaBuilder.equal(projectJoin.get("projectExternalId"), projectExternalId);
        predicates.add(predicateProjectExternalId);

        Join<UserEntity, DocumentEntity> documentJoin = root.join(DOCUMENT, JoinType.INNER);

        if (userFilter.hasUserExternalId()) {
            Predicate predicateUserExternalId = criteriaBuilder.equal(root.get("userExternalId"), userFilter.userExternalId());
            predicates.add(predicateUserExternalId);
        }

        if (userFilter.hasFirstName()) {
            Predicate predicateFirstName = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("firstName")), "%" + userFilter.firstName().toLowerCase() + "%"
            );
            predicates.add(predicateFirstName);
        }

        if (userFilter.hasLastName()) {
            Predicate predicateLastName = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("lastName")), "%" + userFilter.lastName().toLowerCase() + "%"
            );
            predicates.add(predicateLastName);
        }

        if (userFilter.hasEmail()) {
            Predicate predicateEmail = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), "%" + userFilter.email().toLowerCase() + "%"
            );
            predicates.add(predicateEmail);
        }

        if (userFilter.hasCreatedAtFrom()) {
            Predicate predicateCreatedAtFrom = criteriaBuilder.greaterThanOrEqualTo(
                    root.get("createdAt"), userFilter.createdAtFrom()
            );
            predicates.add(predicateCreatedAtFrom);
        }

        if (userFilter.hasCreatedAtTo()) {
            Predicate predicateCreatedAtTo = criteriaBuilder.lessThanOrEqualTo(
                    root.get("createdAt"), userFilter.createdAtTo()
            );
            predicates.add(predicateCreatedAtTo);
        }

        if (userFilter.hasDocument()) {
            DocumentFilter documentFilter = userFilter.document();
            if (documentFilter.hasDocumentType()) {
                Predicate predicateDocumentType = criteriaBuilder.equal(documentJoin.get("type"), documentFilter.type());
                predicates.add(predicateDocumentType);
            }

            if (documentFilter.hasDocumentNumber()) {
                Predicate predicateDocumentNumber = criteriaBuilder.equal(documentJoin.get("number"), documentFilter.number());
                predicates.add(predicateDocumentNumber);
            }
        }

        if (Objects.nonNull(query) && UserEntity.class.equals(query.getResultType())) {
            root.fetch(DOCUMENT, JoinType.INNER);
            root.fetch(PROJECT, JoinType.INNER);
        } else {
            root.join(DOCUMENT, JoinType.INNER);
            root.join(PROJECT, JoinType.INNER);
        }

        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
