package com.popcoadmin.content.repository;

import com.popcoadmin.content.entity.BatchFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchFailureLogRepository extends JpaRepository<BatchFailureLog, Long> {
    List<BatchFailureLog> findByProcessedFalseOrderByFailureTimeDesc();
}