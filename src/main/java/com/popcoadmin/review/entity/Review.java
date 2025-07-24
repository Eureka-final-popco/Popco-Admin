package com.popcoadmin.review.entity;

import com.popcoadmin.content.entity.Content;
import com.popcoadmin.review.entity.enums.ReviewStatus;
import com.popcoadmin.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "content_id", referencedColumnName = "id", nullable = false),
            @JoinColumn(name = "type", referencedColumnName = "type", nullable = false)
    })
    private Content content;

    @Column(name = "review_content")
    private String text;
    private BigDecimal score;
    private Integer likeCount;
    @Column(name = "report_count")
    private Integer report;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status")
    private ReviewStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

}
