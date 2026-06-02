package ru.hits.attackdefenceplatform.core.dashboard.repository.spec;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class FlagSubmissionSpecifications {

    public static Specification<FlagSubmissionEntity> createSpecification(Boolean isCorrect, UUID teamId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (isCorrect != null) {
                predicates.add(criteriaBuilder.equal(root.get("isCorrect"), isCorrect));
            }

            if (teamId != null) {
                predicates.add(criteriaBuilder.equal(root.get("team").get("id"), teamId));
            }

            query.orderBy(criteriaBuilder.asc(root.get("submissionTime")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<FlagSubmissionEntity> createAdminTableSpecification(String search, Boolean isCorrect) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.notEqual(root.get("user").get("isSystem"), true));
            predicates.add(criteriaBuilder.notEqual(root.get("team").get("isSystem"), true));

            if (isCorrect != null) {
                predicates.add(criteriaBuilder.equal(root.get("isCorrect"), isCorrect));
            }

            if (search != null && !search.isBlank()) {
                var pattern = "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
                var flagJoin = root.join("flag", JoinType.LEFT);
                var serviceJoin = flagJoin.join("vulnerableService", JoinType.LEFT);

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("submittedFlag")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("name")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("team").get("name")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("result")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(serviceJoin.get("name")), pattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
