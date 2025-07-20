package com.popcoadmin.quiz.repository;

import com.popcoadmin.quiz.entity.QuizQuestion;
import com.popcoadmin.quiz.entity.key.QuizQuestionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, QuizQuestionId> {
    List<QuizQuestion> findByQuiz_QuizIdOrderByQuestionOrderAsc(Long quizId);
    Optional<QuizQuestion> findByQuiz_QuizIdAndQuestionOrder(Long quizId, Integer questionOrder);
    Optional<QuizQuestion> findById(QuizQuestionId quizQuestionId);
}
