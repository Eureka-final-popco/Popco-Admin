package com.popcoadmin.quiz.repository;

import com.popcoadmin.quiz.entity.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Long> {
    List<QuizOption> findByQuizQuestion_QuestionIdAndQuizQuestion_Quiz_QuizId(Long questionId, Long quizId);
}
