package com.popcoadmin.review.repository;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.content.entity.key.ContentId;
import com.popcoadmin.review.entity.ReviewSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewSummaryRepository extends JpaRepository<ReviewSummary, Long> {
    Optional<ReviewSummary> findByContent(Content content);
}
