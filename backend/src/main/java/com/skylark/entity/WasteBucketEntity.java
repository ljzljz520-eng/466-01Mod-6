package com.skylark.entity;

import com.skylark.enums.WasteCategory;
import com.skylark.enums.WasteStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "waste_bucket")
public class WasteBucketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bucketNo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WasteCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(nullable = false)
    private Boolean hasSpecialIngredients;

    @Column(length = 500)
    private String specialIngredientsRemark;

    @Column(nullable = false)
    private String experimentName;

    @Column(nullable = false)
    private LocalDateTime experimentTime;

    @Column(nullable = false)
    private String labRoom;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(length = 200)
    private String studentContact;

    @Column(unique = true)
    private String qrCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WasteStatus status;

    private LocalDateTime submittedAt;

    private String safetyOfficerId;

    private String safetyOfficerName;

    private LocalDateTime safetyCheckedAt;

    @Column(length = 500)
    private String safetyRemark;

    private String storageOperatorId;

    private String storageOperatorName;

    private LocalDateTime storedAt;

    private String recyclerId;

    private String recyclerName;

    private LocalDateTime collectedAt;

    @Column(length = 1000)
    private String categoryCorrectionRemark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
