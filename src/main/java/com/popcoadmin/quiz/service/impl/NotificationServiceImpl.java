package com.popcoadmin.quiz.service.impl;


import com.popcoadmin.quiz.dto.response.NotificationMessageResponseDto;
import com.popcoadmin.quiz.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String NOTIFICATION_CHANNEL = "notifications";

    public Void publishEventReminder(String eventId, String title, String message, LocalDateTime scheduledTime) {
        NotificationMessageResponseDto notification = NotificationMessageResponseDto.builder()
                .id(UUID.randomUUID().toString())
                .eventId(eventId)
                .title(title)
                .message(message)
                .type(NotificationType.EVENT_REMINDER)
                .scheduledTime(scheduledTime)
                .sentTime(LocalDateTime.now())
                .targetAudience("ALL")
                .build();

        try {
            redisTemplate.convertAndSend(NOTIFICATION_CHANNEL, notification);
            log.info("Published notification for event: {} - {}", eventId, title);
        } catch (Exception e) {
            log.error("Failed to publish notification for event: {}", eventId, e);
        }

        return null;
    }

    public Void publishSystemAnnouncement(String title, String message) {
        NotificationMessageResponseDto notification = new NotificationMessageResponseDto();
        notification.setId(UUID.randomUUID().toString());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(NotificationType.SYSTEM_ANNOUNCEMENT);
        notification.setSentTime(LocalDateTime.now());
        notification.setTargetAudience("ALL");

        try {
            redisTemplate.convertAndSend(NOTIFICATION_CHANNEL, notification);
            log.info("Published system announcement: {}", title);
        } catch (Exception e) {
            log.error("Failed to publish system announcement: {}", title, e);
        }

        return null;
    }
}
