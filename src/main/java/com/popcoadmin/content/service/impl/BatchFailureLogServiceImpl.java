package com.popcoadmin.content.service.impl;

import com.popcoadmin.content.entity.BatchFailureLog;
import com.popcoadmin.content.repository.BatchFailureLogRepository;
import com.popcoadmin.content.service.BatchFailureLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchFailureLogServiceImpl implements BatchFailureLogService {

    private final BatchFailureLogRepository batchFailureLogRepository;
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    private static final Map<String, String> stepToTypeMap = Map.of(
            "popularContentStep_MOVIE", "MOVIE",
            "popularContentStep_TV", "TV",
            "popularContentStep_ALL", "ALL"
    );

    @Value("${slack.bot.token}")
    private String slackBotToken;

    @Value("${slack.channel}")
    private String slackChannel;

    private final WebClient slackWebClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logBatchFailure(StepExecution stepExecution, Exception exception) {
        try {
            BatchFailureLog failureLog = BatchFailureLog.builder()
                    .jobName(stepExecution.getJobExecution().getJobInstance().getJobName())
                    .stepName(stepExecution.getStepName())
                    .errorMessage(exception.getMessage())
                    .build();

            batchFailureLogRepository.save(failureLog);
            sendSlackAlert(stepExecution, exception);

            log.error("배치 실패 로그 저장 완료 - Job: {}, Step: {}, Error: {}",
                    failureLog.getJobName(), failureLog.getStepName(), exception.getMessage());

        } catch (Exception e) {
            log.error("배치 실패 로그 저장 중 오류 발생", e);
        }
    }

    public List<BatchFailureLog> getUnprocessedFailures() {
        return batchFailureLogRepository.findByProcessedFalseOrderByFailureTimeDesc();
    }

    public List<BatchFailureLog> getFailuresByJobAndStep(String jobName, String stepName) {
        return batchFailureLogRepository.findByJobNameAndStepNameOrderByFailureTimeDesc(jobName, stepName);
    }

    public void markAsProcessed(Long failureLogId) {
        BatchFailureLog failureLog = batchFailureLogRepository.findById(failureLogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 실패 로그를 찾을 수 없습니다: " + failureLogId));

        if (failureLog.getProcessed()) {
            throw new IllegalArgumentException("이미 실패된 Job 을 처리하였습니다" + failureLog.getJobName());
        }

        String retryStepType = stepToTypeMap.get(failureLog.getStepName());

        try {
            log.info("인기 콘텐츠 집계 배치 작업 수동 실행");

            JobParameters params = new JobParametersBuilder()
                    .addString("type", retryStepType)
                    .addString("requestDate", LocalDateTime.now().toString()) // 중복 방지
                    .toJobParameters();

            Job job = jobRegistry.getJob("retrySingleStepJob");
            JobExecution jobExecution = jobLauncher.run(job, params);

            if (jobExecution.getStatus().isUnsuccessful()) {
                throw new IllegalStateException("Step 수동 재실행 실패: " + jobExecution.getStatus());
            }

            failureLog.markAsProcessed();
            batchFailureLogRepository.save(failureLog);

            log.info("배치 실패 로그 처리 완료 - ID: {}", failureLogId);

        } catch (Exception e) {
            log.error("인기 콘텐츠 집계 배치 작업 수동 실행 중 오류 발생", e);
        }
    }

    private void sendSlackAlert(StepExecution stepExecution, Exception e) {
        String message = String.format(
                "*🔥🔥 배치 실패 알림*\n" +
                        "> 잡 이름: `%s`\n" +
                        "> 스텝 이름: `%s`\n" +
                        "> 실패 시각: `%s`\n" +
                        "> 예외 메시지: `%s`",
                stepExecution.getJobExecution().getJobInstance().getJobName(),
                stepExecution.getStepName(),
                LocalDateTime.now(),
                e.getMessage()
        );

        Map<String, Object> payload = Map.of(
                "channel", slackChannel,
                "text", message
        );

        slackWebClient.post()
                .uri("/chat.postMessage")
                .header("Authorization", "Bearer " + slackBotToken)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Slack response: {}", response))
                .doOnError(error -> log.error("Slack error: {}", error.getMessage()))
                .subscribe();
    }
}