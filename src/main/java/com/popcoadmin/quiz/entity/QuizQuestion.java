package com.popcoadmin.quiz.entity;

import com.popcoadmin.quiz.dto.request.QuizQuestionRequestDto;
import com.popcoadmin.quiz.entity.key.QuizQuestionId;
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
@Table(name = "quiz_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class QuizQuestion {

    @EmbeddedId
    private QuizQuestionId questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("quizId")
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer questionOrder;

    private String imgPath;

    @Column(nullable = false)
    private LocalDateTime finishedAt;

    @Column(nullable = false)
    private Integer firstCapacity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumns({
            @JoinColumn(name = "question_id", referencedColumnName = "question_id"),
            @JoinColumn(name = "quiz_id", referencedColumnName = "quiz_id")
    })
    private List<QuizOption> options = new ArrayList<>();

    public static QuizQuestion of(QuizQuestionRequestDto request) {
        return QuizQuestion.builder()
                .questionOrder(request.getQuestionOrder())
                .imgPath(request.getImgPath())
                .finishedAt(request.getFinishedAt())
                .content(request.getContent())
                .firstCapacity(request.getFirstCapacity())
                .build();
    }

    public void update(QuizQuestionRequestDto request) {
        if (request.getQuestionOrder() != null) {
            this.questionOrder = request.getQuestionOrder();
        }
        if (StringUtils.hasText(request.getImgPath())) {
            this.imgPath = request.getImgPath();
        }
        if (request.getFinishedAt() != null) {
            this.finishedAt = request.getFinishedAt();
        }
        if (request.getFirstCapacity() != null) {
            this.firstCapacity = request.getFirstCapacity();
        }
        if (StringUtils.hasText(request.getContent())) {
            this.content = request.getContent();
        }
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void addOption(QuizOption option) {
        this.options.add(option);
        option.setQuizQuestion(this);
    }

    public void removeOption(QuizOption option) {
        this.options.remove(option);
        option.setQuizQuestion(null);
    }

}
