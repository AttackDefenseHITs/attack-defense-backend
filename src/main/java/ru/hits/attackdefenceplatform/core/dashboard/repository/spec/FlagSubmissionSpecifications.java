package ru.hits.attackdefenceplatform.core.dashboard.repository.spec;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.hits.attackdefenceplatform.core.dashboard.repository.FlagSubmissionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlagSubmissionSpecifications {

    public static Specification<FlagSubmissionEntity> createSpecification(Boolean isCorrect, UUID teamId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (isCorrect != null) {
                predicates.add(criteriaBuilder.equal(root.get("isCorrect"), isCorrect));
            }

            if (teamId != null) {
                predicates.add(criteriaBuilder.equal(root.get("teamMember").get("team").get("id"), teamId));
            }

            query.orderBy(criteriaBuilder.asc(root.get("submissionTime")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
