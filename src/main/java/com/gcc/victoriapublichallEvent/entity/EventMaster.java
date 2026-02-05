package com.gcc.victoriapublichallEvent.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_master")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_discription")
    private String eventDiscription;

    @Column(name = "event_date")
    private String eventDate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_delete")
    private boolean isDelete = false;

    @Column(name = "cdate")
    private LocalDateTime cdate = LocalDateTime.now();

    @Column(name = "is_publish")
    private boolean isPublish = false;

    @Column(name = "event_category_id")
    private Integer eventCategoryId;

    @Column(name = "event_type_id")
    private Integer eventTypeId;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "event_type_id", insertable = false, updatable = false)
    private EventTypeMaster eventTypeMaster;

    @Column(name = "event_image_url")
    private String eventImageUrl;

    @Transient
    private String encryptedEventId;
}
