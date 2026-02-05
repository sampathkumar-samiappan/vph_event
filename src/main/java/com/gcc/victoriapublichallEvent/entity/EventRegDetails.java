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
@Table(name = "event_reg_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRegDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "mob_no")
    private String mobNo;

    @Column(name = "no_of_people")
    private Integer noOfPeople;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "booking_flag")
    private String bookingFlag;

    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "ref_id")
    private String refId;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_delete")
    private boolean isDelete = false;

    @Column(name = "cdate")
    private LocalDateTime cdate = LocalDateTime.now();

    @Column(name = "source")
    private Boolean source;

    // Additional fields for Razorpay usually helpful
    // @Column(name = "payment_id")
    // private String paymentId;

    // @Column(name = "order_id")
    // private String orderId;
}
