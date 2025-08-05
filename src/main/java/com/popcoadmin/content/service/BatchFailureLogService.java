package com.popcoadmin.content.service;

import com.popcoadmin.content.entity.BatchFailureLog;
import org.springframework.batch.core.*;
import java.util.List;

public interface BatchFailureLogService {

    void logBatchFailure(StepExecution stepExecution, Exception exception);

    List<BatchFailureLog> getUnprocessedFailures();

    List<BatchFailureLog> getFailuresByJobAndStep(String jobName, String stepName);

    void markAsProcessed(Long failureLogId);

}