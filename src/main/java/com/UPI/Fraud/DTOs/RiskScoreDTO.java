package com.UPI.Fraud.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskScoreDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private int totalRiskScore;
    private String riskLevel;       // LOW, MEDIUM, HIGH, CRITICAL
    private List<String> riskFactors;
    private long loginCount24h;
    private long distinctIps24h;
    private long distinctDevices24h;
    private boolean hasSuspiciousActivity;
}