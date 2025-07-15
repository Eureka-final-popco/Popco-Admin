package com.popcoadmin.quiz.entity;

import com.popcoadmin.quiz.dto.request.QuizRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    private String bannerPath;

    @Column(nullable = false)
    private Integer roundCount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizQuestion> questions = new ArrayList<>();

    public static Quiz of(QuizRequestDto request) {
        return Quiz.builder()
                .name(request.getName())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .bannerPath(request.getBannerPath())
                .roundCount(request.getRoundCount())
                .build();
    }

    public void update(QuizRequestDto request) {
        if(StringUtils.hasText(request.getName())) {
            this.name = request.getName();
        }
        if(request.getStartAt() != null) {
            this.endAt = request.getStartAt();
        }
        if(request.getEndAt() != null) {
            this.endAt = request.getEndAt();
        }
        if(StringUtils.hasText(request.getBannerPath())) {
            this.bannerPath = request.getBannerPath();
        }
        if(request.getRoundCount() != null) {
            this.roundCount = request.getRoundCount();
        }
    }

    public void addQuestion(QuizQuestion question) {
        this.questions.add(question);
        question.setQuiz(this);
    }

    public void removeQuestion(QuizQuestion question) {
        this.questions.remove(question);
        question.setQuiz(null);
    }

}

