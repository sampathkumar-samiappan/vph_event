package com.gcc.victoriapublichallEvent.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_time_slot")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "event_timing")
    private String eventTiming;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_delete")
    private boolean isDelete = false;

    @Column(name = "cdate")
    private LocalDateTime cdate = LocalDateTime.now();

    // Helper to track available seats if needed, or query dynamically
}
