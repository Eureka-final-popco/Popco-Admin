package com.popcoadmin.quiz.service.impl;

import com.popcoadmin.quiz.entity.Quiz;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import com.popcoadmin.quiz.repository.QuizRepository;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuizNotificationServiceImpl {

    private final QuizRepository quizRepository;
    private final NotificationServiceImpl notificationService;

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public List<Quiz> getUpcomingQuizzes() {
        return quizRepository.findUpcomingQuizzes(LocalDateTime.now());
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizId));
    }

    public void sendTestNotification(String title, String message) {
        notificationService.publishSystemAnnouncement(title, message);
        log.info("Sent test notification: {}", title);
    }

    public void sendQuizNotification(Long quizId) {
        Quiz quiz = getQuizById(quizId);

        String message = String.format("'%s' 퀴즈가 곧 시작됩니다!", quiz.getName());
        notificationService.publishEventReminder(
                quiz.getQuizId().toString(),
                "퀴즈 알림",
                message,
                quiz.getStartAt()
        );

        log.info("Sent manual notification for quiz: {} (id: {})", quiz.getName(), quizId);
    }
}
