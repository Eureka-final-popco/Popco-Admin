package com.popcoadmin.quiz.repository;

import com.popcoadmin.quiz.entity.UserQuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuizAnswerRepository extends JpaRepository<UserQuizAnswer, Long> {
}
