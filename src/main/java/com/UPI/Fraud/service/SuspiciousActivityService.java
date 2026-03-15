package com.UPI.Fraud.service;

import com.UPI.Fraud.DTOs.RiskScoreDTO;
import com.UPI.Fraud.DTOs.SuspiciousActivityDTO;
import com.UPI.Fraud.entities.SuspiciousActivity;
import com.UPI.Fraud.entities.User;
import com.UPI.Fraud.repository.SuspiciousActivityRepository;
import com.UPI.Fraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuspiciousActivityService {

    private final SuspiciousActivityRepository suspiciousActivityRepository;
    private final UserRepository userRepository;
    private final RiskScoringService riskScoringService;

    public List<SuspiciousActivityDTO> getAllUnresolved() {
        return suspiciousActivityRepository.findAllUnresolved()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<SuspiciousActivityDTO> getActivitiesByUser(Long userId) {
        return suspiciousActivityRepository.findRecentByUser(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<SuspiciousActivityDTO> getHighRiskActivities(int threshold) {
        return suspiciousActivityRepository.findByRiskScoreGreaterThanEqual(threshold)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public SuspiciousActivityDTO resolveActivity(Long activityId) {
        SuspiciousActivity activity = suspiciousActivityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));
        activity.setResolved(true);
        return toDTO(suspiciousActivityRepository.save(activity));
    }

    @Transactional
    public SuspiciousActivityDTO createManualFlag(Long userId, String activityType,
                                                  int riskScore, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        SuspiciousActivity activity = SuspiciousActivity.builder()
                .user(user)
                .activityType(activityType)
                .riskScore(riskScore)
                .description(description)
                .resolved(false)
                .build();

        return toDTO(suspiciousActivityRepository.save(activity));
    }

    public RiskScoreDTO getRiskScoreForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return riskScoringService.calculateRiskScore(user);
    }

    public List<RiskScoreDTO> getRiskScoresAllUsers() {
        return userRepository.findAll().stream()
                .map(riskScoringService::calculateRiskScore)
                .collect(Collectors.toList());
    }

    private SuspiciousActivityDTO toDTO(SuspiciousActivity sa) {
        return SuspiciousActivityDTO.builder()
                .activityId(sa.getActivityId())
                .userId(sa.getUser().getUserId())
                .userName(sa.getUser().getName())
                .deviceId(sa.getDevice() != null ? sa.getDevice().getDeviceId() : null)
                .ipId(sa.getIpAddress() != null ? sa.getIpAddress().getIpId() : null)
                .ipAddress(sa.getIpAddress() != null ? sa.getIpAddress().getIpAddress() : null)
                .activityType(sa.getActivityType())
                .riskScore(sa.getRiskScore())
                .description(sa.getDescription())
                .detectedAt(sa.getDetectedAt())
                .resolved(sa.getResolved())
                .build();
    }
}