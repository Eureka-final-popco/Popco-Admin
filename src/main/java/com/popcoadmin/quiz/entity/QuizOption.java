package com.popcoadmin.quiz.entity;

import com.popcoadmin.quiz.dto.request.QuizOptionRequestDto;
import com.popcoadmin.quiz.entity.key.QuizOptionId;
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

    @EmbeddedId
    private QuizOptionId optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "question_id", referencedColumnName = "question_id", insertable = false, updatable = false),
            @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id", insertable = false, updatable = false)
    })
    private QuizQuestion quizQuestion;

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
        QuizOptionId optionId = QuizOptionId.of(
                null,
                quizQuestion.getQuestionId().getQuestionId(),
                quiz.getQuizId()
        );

        return QuizOption.builder()
                .optionId(optionId)
                .isCorrect(request.getIsCorrect())
                .content(request.getContent())
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
        if (this.optionId == null) {
            this.optionId = new QuizOptionId();
        }
        this.optionId.setQuestionId(quizQuestion.getQuestionId().getQuestionId());
    }

    public void setQuiz(Quiz quiz) {
        if (this.optionId == null) {
            this.optionId = new QuizOptionId();
        }
        this.optionId.setQuizId(quiz.getQuizId());
    }
}
