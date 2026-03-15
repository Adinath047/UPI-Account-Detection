package com.UPI.Fraud.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspiciousActivityDTO {
    private Long activityId;
    private Long userId;
    private String userName;
    private Long deviceId;
    private Long ipId;
    private String ipAddress;
    private String activityType;
    private Integer riskScore;
    private String description;
    private LocalDateTime detectedAt;
    private Boolean resolved;
}