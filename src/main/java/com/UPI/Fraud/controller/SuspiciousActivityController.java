package com.UPI.Fraud.controller;

import com.UPI.Fraud.DTOs.RiskScoreDTO;
import com.UPI.Fraud.DTOs.SuspiciousActivityDTO;
import com.UPI.Fraud.service.SuspiciousActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suspicious")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SuspiciousActivityController {

    private final SuspiciousActivityService suspiciousActivityService;

    /**
     * Get all unresolved suspicious activities
     */
    @GetMapping("/unresolved")
    public ResponseEntity<List<SuspiciousActivityDTO>> getUnresolved() {
        return ResponseEntity.ok(suspiciousActivityService.getAllUnresolved());
    }

    /**
     * Get suspicious activities for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SuspiciousActivityDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(suspiciousActivityService.getActivitiesByUser(userId));
    }

    /**
     * Get all activities above a risk score threshold
     */
    @GetMapping("/high-risk")
    public ResponseEntity<List<SuspiciousActivityDTO>> getHighRisk(
            @RequestParam(defaultValue = "60") int threshold) {
        return ResponseEntity.ok(suspiciousActivityService.getHighRiskActivities(threshold));
    }

    /**
     * Mark an activity as resolved
     */
    @PatchMapping("/{activityId}/resolve")
    public ResponseEntity<SuspiciousActivityDTO> resolve(@PathVariable Long activityId) {
        return ResponseEntity.ok(suspiciousActivityService.resolveActivity(activityId));
    }

    /**
     * Manually flag suspicious activity
     */
    @PostMapping("/flag")
    public ResponseEntity<SuspiciousActivityDTO> manualFlag(@RequestBody Map<String, Object> body) {
        Long userId = Long.parseLong(body.get("userId").toString());
        String activityType = body.get("activityType").toString();
        int riskScore = Integer.parseInt(body.get("riskScore").toString());
        String description = body.get("description").toString();
        return ResponseEntity.ok(suspiciousActivityService.createManualFlag(userId, activityType, riskScore, description));
    }

    /**
     * Get risk score for a specific user
     */
    @GetMapping("/risk-score/{userId}")
    public ResponseEntity<RiskScoreDTO> getRiskScore(@PathVariable Long userId) {
        return ResponseEntity.ok(suspiciousActivityService.getRiskScoreForUser(userId));
    }

    /**
     * Get risk scores for ALL users (dashboard overview)
     */
    @GetMapping("/risk-scores/all")
    public ResponseEntity<List<RiskScoreDTO>> getAllRiskScores() {
        return ResponseEntity.ok(suspiciousActivityService.getRiskScoresAllUsers());
    }
}