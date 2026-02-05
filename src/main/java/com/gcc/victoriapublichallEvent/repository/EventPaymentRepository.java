package com.gcc.victoriapublichallEvent.repository;

import com.gcc.victoriapublichallEvent.entity.EventPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventPaymentRepository extends JpaRepository<EventPayment, Integer> {
}
