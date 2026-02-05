package com.gcc.victoriapublichallEvent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event_order_log")
public class EventOrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_info", length = 1000)
    private String orderInfo;

    @Column(name = "order_id", length = 45)
    private String orderId;

    @Column(name = "ref_id", length = 45)
    private String refId;

    @CreationTimestamp
    @Column(name = "cdate", updatable = false)
    private LocalDateTime cdate;

    @Column(name = "order_status", length = 50)
    private String orderStatus;

    @Column(name = "pay_attempt")
    private Integer payAttempt = 0;
}
