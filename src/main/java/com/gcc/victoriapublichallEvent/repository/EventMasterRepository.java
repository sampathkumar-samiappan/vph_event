package com.gcc.victoriapublichallEvent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcc.victoriapublichallEvent.entity.EventMaster;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventMasterRepository extends JpaRepository<EventMaster, Integer> {
    Optional<EventMaster> findByIsActiveAndIsPublish(boolean isActive, boolean isPublish);

    List<EventMaster> findAllByIsActiveAndIsPublish(boolean isActive, boolean isPublish);

    List<EventMaster> findAllByIsActiveAndIsPublishAndEventDateGreaterThanEqualOrderByEventDateAsc(boolean isActive,
            boolean isPublish, String eventDate);
}
