package com.popcoadmin.content.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 인기 콘텐츠 결과 엔티티
@Entity
@Table(name = "popular_contents")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DailyPopularContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @LastModifiedDate
    @Column(name = "ranked_date", nullable = false)
    private LocalDate rankedDate;

    @Column(name = "ranking", nullable = false)
    private Integer ranking;

}
