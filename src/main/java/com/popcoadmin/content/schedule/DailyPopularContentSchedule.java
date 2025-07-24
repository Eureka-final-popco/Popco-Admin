package com.popcoadmin.content.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyPopularContentSchedule {
    private final JobLauncher jobLauncher;
    private final Job popularContentJob;

    @Scheduled(cron = "0 5 2 * * *")
    public void runPopularContentJob() {
        try {
            log.info("인기 콘텐츠 집계 배치 작업 시작");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("executionDate", LocalDate.now().toString())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(popularContentJob, jobParameters);

            log.info("인기 콘텐츠 집계 배치 작업 완료. Status: {}",
                    jobExecution.getStatus());

        } catch (Exception e) {
            log.error("인기 콘텐츠 집계 배치 작업 실행 중 오류 발생", e);
        }
    }

    /**
     * 수동 실행을 위한 메서드 (테스트 용도)
     */
    public void runManually() {
        try {
            log.info("인기 콘텐츠 집계 배치 작업 수동 실행");

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("executionDate", LocalDate.now().toString())
                    .addString("mode", "manual")
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(popularContentJob, jobParameters);

            log.info("인기 콘텐츠 집계 배치 작업 수동 실행 완료. Status: {}",
                    jobExecution.getStatus());

        } catch (Exception e) {
            log.error("인기 콘텐츠 집계 배치 작업 수동 실행 중 오류 발생", e);
        }
    }
}
