package com.gcc.victoriapublichallEvent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcc.victoriapublichallEvent.entity.EventTimeSlot;

import java.util.List;

@Repository
public interface EventTimeSlotRepository extends JpaRepository<EventTimeSlot, Integer> {
    List<EventTimeSlot> findByEventIdAndIsActiveTrue(Integer eventId);
}
