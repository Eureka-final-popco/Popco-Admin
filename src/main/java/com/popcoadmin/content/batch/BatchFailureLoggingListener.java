package com.popcoadmin.content.batch;

import com.popcoadmin.content.service.BatchFailureLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BatchFailureLoggingListener implements StepExecutionListener {

    private final BatchFailureLogService BatchFailureLogService;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if (stepExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {

            // 실패한 경우 예외 정보 추출
            Exception lastException = getLastException(stepExecution);
            if (lastException != null) {
                BatchFailureLogService.logBatchFailure(stepExecution, lastException);
            } else {
                // 예외가 없는 경우 일반 실패로 처리
                Exception genericException = new RuntimeException("Step failed without specific exception");
                BatchFailureLogService.logBatchFailure(stepExecution, genericException);
            }
        }

        return stepExecution.getExitStatus();
    }

    private Exception getLastException(StepExecution stepExecution) {
        if (!stepExecution.getFailureExceptions().isEmpty()) {
            Throwable lastFailure = stepExecution.getFailureExceptions().get(
                    stepExecution.getFailureExceptions().size() - 1);

            if (lastFailure instanceof Exception) {
                return (Exception) lastFailure;
            } else {
                return new RuntimeException(lastFailure);
            }
        }
        return null;
    }
}

