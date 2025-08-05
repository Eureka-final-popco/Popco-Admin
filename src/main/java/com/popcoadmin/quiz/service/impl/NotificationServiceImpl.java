package com.popcoadmin.quiz.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popcoadmin.quiz.dto.response.NotificationMessageResponseDto;
import com.popcoadmin.quiz.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String NOTIFICATION_CHANNEL = "notifications";

    public Void publishEventReminder(String eventId, String title, String message, LocalDateTime scheduledTime) {
        Long remainMin = calculateRemainingMinutes(scheduledTime);
        NotificationMessageResponseDto notification = NotificationMessageResponseDto.builder()
                .id(UUID.randomUUID().toString())
                .eventId(eventId)
                .title(title)
                .message(message)
                .type(NotificationType.EVENT_REMINDER)
                .scheduledTime(scheduledTime)
                .sentTime(LocalDateTime.now())
                .remainMin(remainMin)
                .targetAudience("ALL")
                .build();

        try {
            String notificationJson = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend(NOTIFICATION_CHANNEL, notificationJson);
            log.info("Published notification for event: {} - {}", eventId, title);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize notification object to JSON for event: {}", eventId, e);
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
            String notificationJson = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend(NOTIFICATION_CHANNEL, notificationJson);
            log.info("Published system announcement: {}", title);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize notification object to JSON for announcement: {}", title, e);
        } catch (Exception e) {
            log.error("Failed to publish system announcement: {}", title, e);
        }

        return null;
    }

    public long calculateRemainingMinutes(LocalDateTime futureDateTime) {
        LocalDateTime now = LocalDateTime.now();
        if (futureDateTime == null || !futureDateTime.isAfter(now)) {
            return 0L;
        }
        Duration duration = Duration.between(now, futureDateTime);
        return duration.toMinutes();
    }
}