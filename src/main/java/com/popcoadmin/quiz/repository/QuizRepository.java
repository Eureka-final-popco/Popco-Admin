package com.popcoadmin.quiz.repository;

import com.popcoadmin.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByName(String name);

    @Query("SELECT q FROM Quiz q WHERE q.startAt BETWEEN :now AND :endTime ORDER BY q.startAt ASC")
    List<Quiz> findQuizzesStartingWithin(
            @Param("now") LocalDateTime now,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT q FROM Quiz q WHERE q.startAt > :now ORDER BY q.startAt ASC")
    List<Quiz> findUpcomingQuizzes(@Param("now") LocalDateTime now);

    @Query("SELECT q FROM Quiz q WHERE DATE(q.startAt) = DATE(:today) ORDER BY q.startAt ASC")
    List<Quiz> findQuizzesToday(@Param("today") LocalDateTime today);

}
