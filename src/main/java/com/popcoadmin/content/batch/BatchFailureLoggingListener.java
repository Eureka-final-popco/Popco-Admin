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
        long writeCount = stepExecution.getWriteCount();

        if (writeCount < 5) {
            Exception writeCountException = new IllegalStateException("추천 콘텐츠가 부족합니다: writeCount= "+ writeCount);
            BatchFailureLogService.logBatchFailure(stepExecution, writeCountException);
            stepExecution.setExitStatus(ExitStatus.FAILED);
        }

        if (stepExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
            Exception lastException = getLastException(stepExecution);

            if (lastException != null) {
                BatchFailureLogService.logBatchFailure(stepExecution, lastException);
            } else {
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

