package com.gcc.victoriapublichallEvent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcc.victoriapublichallEvent.entity.EventTypeMaster;

@Repository
public interface EventTypeMasterRepository extends JpaRepository<EventTypeMaster, Integer> {
}
