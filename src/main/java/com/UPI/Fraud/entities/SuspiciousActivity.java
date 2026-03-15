package com.UPI.Fraud.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "suspicious_activity")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuspiciousActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_id")
    private IpAddress ipAddress;

    @Column(name = "activity_type", length = 100)
    private String activityType;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "detected_at", updatable = false)
    private LocalDateTime detectedAt;

    @Column(name = "resolved")
    @Builder.Default
    private Boolean resolved = false;
}