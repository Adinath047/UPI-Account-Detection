package com.UPI.Fraud.service;

import com.UPI.Fraud.DTOs.RiskScoreDTO;
import com.UPI.Fraud.entities.*;
import com.UPI.Fraud.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskScoringService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final IpAddressRepository ipAddressRepository;
    private final DeviceRepository deviceRepository;
    private final SuspiciousActivityRepository suspiciousActivityRepository;

    // Risk thresholds
    private static final int MAX_LOGINS_24H = 10;
    private static final int MAX_IPS_24H = 3;
    private static final int MAX_DEVICES_24H = 2;
    private static final int SCORE_HIGH_LOGIN_FREQ = 25;
    private static final int SCORE_MULTIPLE_IPS = 30;
    private static final int SCORE_MULTIPLE_DEVICES = 20;
    private static final int SCORE_BLACKLISTED_IP = 40;
    private static final int SCORE_NEW_DEVICE = 15;

    /**
     * Calculate a comprehensive risk score for a user.
     * Score 0-100: LOW(0-29), MEDIUM(30-59), HIGH(60-79), CRITICAL(80-100)
     */
    public RiskScoreDTO calculateRiskScore(User user) {
        int score = 0;
        List<String> riskFactors = new ArrayList<>();
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);

        // Factor 1: Login frequency in last 24h
        long loginCount = loginHistoryRepository.countLoginsAfter(user.getUserId(), last24h);
        if (loginCount > MAX_LOGINS_24H) {
            score += SCORE_HIGH_LOGIN_FREQ;
            riskFactors.add("High login frequency: " + loginCount + " logins in 24h");
        }

        // Factor 2: Multiple IPs in last 24h
        long distinctIps = loginHistoryRepository.countDistinctIpsAfter(user.getUserId(), last24h);
        if (distinctIps > MAX_IPS_24H) {
            score += SCORE_MULTIPLE_IPS;
            riskFactors.add("Multiple IPs detected: " + distinctIps + " distinct IPs in 24h");
        }

        // Factor 3: Multiple devices in last 24h
        long distinctDevices = loginHistoryRepository.countDistinctDevicesAfter(user.getUserId(), last24h);
        if (distinctDevices > MAX_DEVICES_24H) {
            score += SCORE_MULTIPLE_DEVICES;
            riskFactors.add("Multiple devices detected: " + distinctDevices + " devices in 24h");
        }

        // Factor 4: Blacklisted IPs
        long blacklistedIps = ipAddressRepository.findByUser_UserId(user.getUserId())
                .stream().filter(ip -> "BLACKLISTED".equals(ip.getStatus())).count();
        if (blacklistedIps > 0) {
            score += SCORE_BLACKLISTED_IP;
            riskFactors.add("Blacklisted IP address detected");
        }

        // Factor 5: Suspicious IPs
        long suspiciousIps = ipAddressRepository.findByUser_UserId(user.getUserId())
                .stream().filter(ip -> "SUSPICIOUS".equals(ip.getStatus())).count();
        if (suspiciousIps > 0) {
            score += 20;
            riskFactors.add("Suspicious IP(s) detected: " + suspiciousIps);
        }

        // Factor 6: Total device count (many devices = risky)
        long totalDevices = deviceRepository.countByUser_UserId(user.getUserId());
        if (totalDevices > 5) {
            score += SCORE_NEW_DEVICE;
            riskFactors.add("Abnormal device count: " + totalDevices + " registered devices");
        }

        // Cap score at 100
        score = Math.min(score, 100);

        String riskLevel = determineRiskLevel(score);
        boolean hasSuspiciousActivity = !suspiciousActivityRepository.findRecentByUser(user.getUserId()).isEmpty();

        log.info("Risk score for user {}: {} ({})", user.getUserId(), score, riskLevel);

        return RiskScoreDTO.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .totalRiskScore(score)
                .riskLevel(riskLevel)
                .riskFactors(riskFactors)
                .loginCount24h(loginCount)
                .distinctIps24h(distinctIps)
                .distinctDevices24h(distinctDevices)
                .hasSuspiciousActivity(hasSuspiciousActivity)
                .build();
    }

    public String determineRiskLevel(int score) {
        if (score >= 80) return "CRITICAL";
        if (score >= 60) return "HIGH";
        if (score >= 30) return "MEDIUM";
        return "LOW";
    }
}