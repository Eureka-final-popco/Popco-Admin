package com.popcoadmin.event.repository;

import com.popcoadmin.event.entity.EventQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventQuestionRepository extends JpaRepository<EventQuestion, Long> {
    Optional<EventQuestion> findByEvent_EventIdAndSortOrder(Long eventId, Integer sortOrder);
    List<EventQuestion> findByEvent_EventIdOrderBySortOrderAsc(Long eventId);
}
