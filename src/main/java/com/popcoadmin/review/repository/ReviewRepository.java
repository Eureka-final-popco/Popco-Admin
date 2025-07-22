package com.popcoadmin.review.repository;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.review.entity.Review;
import com.popcoadmin.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Boolean existsReviewByContentAndUser(Content content, User user);
}
