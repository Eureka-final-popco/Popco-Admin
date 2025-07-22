package com.popcoadmin.review.repository;

import com.popcoadmin.review.entity.Review;
import com.popcoadmin.review.entity.ReviewReaction;
import com.popcoadmin.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {
    Optional<ReviewReaction> findByReviewAndUser(Review review, User user);
    Integer countByReview(Review review);
}
