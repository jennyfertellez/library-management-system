package com.jennifertellez.library.repository;

import com.jennifertellez.library.model.Book;
import com.jennifertellez.library.model.BookSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class BookSpecification {

    public static Specification<Book> withCriteria(BookSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search term - searches in title, author, and description
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().isEmpty()) {
                String searchPattern = "%" + criteria.getSearchTerm().toLowerCase() + "%";
                Predicate titlePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate authorPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("author")), searchPattern);
                Predicate descriptionPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")), searchPattern);

                predicates.add(criteriaBuilder.or(titlePredicate, authorPredicate, descriptionPredicate));
            }

            // Filter by status
            if (criteria.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // Filter by author
            if (criteria.getAuthor() != null && !criteria.getAuthor().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("author")),
                        "%" + criteria.getAuthor().toLowerCase() + "%"));
            }

            // Filter by rating range
            if (criteria.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("rating"), criteria.getMinRating()));
            }
            if (criteria.getMaxRating() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("rating"), criteria.getMaxRating()));
            }

            // Filter by published year range
            if (criteria.getMinYear() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("publishedDate"), criteria.getMinYear().toString()));
            }
            if (criteria.getMaxYear() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("publishedDate"), criteria.getMaxYear().toString()));
            }

            // Filter by ISBN existence
            if (criteria.getHasIsbn() != null) {
                if (criteria.getHasIsbn()) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("isbn")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("isbn")));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}