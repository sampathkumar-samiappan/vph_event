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
@Table(name = "event_type_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTypeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_delete")
    private boolean isDelete = false;

    @Column(name = "cdate")
    private LocalDateTime cdate = LocalDateTime.now();
}
