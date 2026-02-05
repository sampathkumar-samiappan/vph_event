package com.gcc.victoriapublichallEvent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcc.victoriapublichallEvent.entity.EventRegDetails;

@Repository
public interface EventRegDetailsRepository extends JpaRepository<EventRegDetails, Integer> {
    EventRegDetails findByRefId(String refId);

    java.util.List<EventRegDetails> findByMobNoAndIsDeleteFalse(String mobNo);
}
