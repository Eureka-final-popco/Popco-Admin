package com.popcoadmin.quiz.repository;

import com.popcoadmin.quiz.entity.QuizOption;
import com.popcoadmin.quiz.entity.QuizQuestion;
import com.popcoadmin.quiz.entity.key.QuizOptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, QuizOptionId> {
    List<QuizOption> findByQuizQuestion(QuizQuestion quizQuestion);
}
