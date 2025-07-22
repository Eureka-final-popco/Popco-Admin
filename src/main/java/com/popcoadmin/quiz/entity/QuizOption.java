package com.popcoadmin.quiz.entity;

import com.popcoadmin.quiz.dto.request.QuizOptionRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_options")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class QuizOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Boolean isCorrect;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public static QuizOption of(QuizOptionRequestDto request, QuizQuestion quizQuestion, Quiz quiz) {
        return QuizOption.builder()
                .isCorrect(request.getIsCorrect())
                .content(request.getContent())
                .quizQuestion(quizQuestion)
                .quiz(quiz)
                .build();
    }

    public void update(QuizOptionRequestDto request) {
        if (request.getIsCorrect() != null) {
            this.isCorrect = request.getIsCorrect();
        }
        if (StringUtils.hasText(request.getContent())) {
            this.content = request.getContent();
        }
    }

    public void setQuizQuestion(QuizQuestion quizQuestion) {
        this.quizQuestion = quizQuestion;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
