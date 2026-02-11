package com.gcc.victoriapublichallEvent.repository;

import com.gcc.victoriapublichallEvent.entity.EventOrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventOrderLogRepository extends JpaRepository<EventOrderLog, Integer> {
    EventOrderLog findByOrderId(String orderId);
}
