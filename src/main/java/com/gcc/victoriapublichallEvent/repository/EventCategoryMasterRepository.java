package com.gcc.victoriapublichallEvent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcc.victoriapublichallEvent.entity.EventCategoryMaster;

@Repository
public interface EventCategoryMasterRepository extends JpaRepository<EventCategoryMaster, Integer> {
}
