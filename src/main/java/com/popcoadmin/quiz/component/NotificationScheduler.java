package com.popcoadmin.quiz.component;

import com.popcoadmin.quiz.entity.Quiz;
import com.popcoadmin.quiz.repository.QuizRepository;
import com.popcoadmin.quiz.service.impl.NotificationServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final QuizRepository quizRepository;
    private final NotificationServiceImpl notificationService;

    @Scheduled(fixedRate = 60000) // 1분
    @Transactional(readOnly = true)
    public void sendUpcomingQuizNotifications() {
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(seoulZoneId);
        LocalDateTime tenMinutesFromNow = now.plusMinutes(10);

        List<Quiz> upcomingQuizzes = quizRepository.findQuizzesStartingWithin(now, tenMinutesFromNow);

        for (Quiz quiz : upcomingQuizzes) {
            try {
                long minutesUntilStart = Duration.between(now, quiz.getStartAt()).toMinutes();

                // 0분 미만이면 스킵 (이미 시작된 퀴즈)
                if (minutesUntilStart < 0) {
                    continue;
                }

                String message;
                if (minutesUntilStart <= 10) {
                    message = String.format("'%s' 퀴즈가 곧 시작됩니다! 지금 바로 참여하세요!", quiz.getName());
                } else {
                    message = String.format("'%s' 퀴즈가 %d분 후에 시작됩니다! 미리 접속해보세요!",
                            quiz.getName(), minutesUntilStart);
                }

                notificationService.publishEventReminder(
                        quiz.getQuizId().toString(),
                        "퀴즈 시작 알림",
                        message,
                        quiz.getStartAt()
                );

                log.debug("Notification sent for quiz: {} ({}분 후 시작)", quiz.getName(), minutesUntilStart);

            } catch (Exception e) {
                log.error("Failed to send notification for quiz: {} (id: {})", quiz.getName(), quiz.getQuizId(), e);
            }
        }

        if (!upcomingQuizzes.isEmpty()) {
            log.info("Sent notifications for {} upcoming quizzes", upcomingQuizzes.size());
        }
    }
}
