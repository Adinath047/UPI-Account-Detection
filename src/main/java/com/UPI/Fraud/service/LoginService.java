package com.UPI.Fraud.service;

import com.UPI.Fraud.DTOs.LoginRequestDTO;
import com.UPI.Fraud.DTOs.RiskScoreDTO;
import com.UPI.Fraud.entities.*;
import com.UPI.Fraud.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final IpAddressRepository ipAddressRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final SuspiciousActivityRepository suspiciousActivityRepository;
    private final RiskScoringService riskScoringService;

    @Transactional
    public RiskScoreDTO processLogin(LoginRequestDTO request) {
        // 1. Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Register or update device
        Device device = registerDevice(user, request);

        // 3. Register or update IP
        IpAddress ip = registerIpAddress(user, request);

        // 4. Record login history
        LoginHistory loginHistory = LoginHistory.builder()
                .user(user)
                .device(device)
                .ipAddress(ip)
                .loginTime(LocalDateTime.now())
                .status("SUCCESS")
                .build();
        loginHistoryRepository.save(loginHistory);

        // 5. Calculate risk score
        RiskScoreDTO riskScore = riskScoringService.calculateRiskScore(user);

        // 6. Auto-flag suspicious activity if risk is HIGH or CRITICAL
        if ("HIGH".equals(riskScore.getRiskLevel()) || "CRITICAL".equals(riskScore.getRiskLevel())) {
            flagSuspiciousActivity(user, device, ip, riskScore);
        }

        return riskScore;
    }

    private Device registerDevice(User user, LoginRequestDTO request) {
        return deviceRepository.findByDeviceHash(request.getDeviceHash())
                .map(d -> {
                    d.setLastSeen(LocalDateTime.now());
                    return deviceRepository.save(d);
                })
                .orElseGet(() -> deviceRepository.save(Device.builder()
                        .user(user)
                        .deviceHash(request.getDeviceHash())
                        .browser(request.getBrowser())
                        .os(request.getOs())
                        .platform(request.getPlatform())
                        .screenResolution(request.getScreenResolution())
                        .timezone(request.getTimezone())
                        .lastSeen(LocalDateTime.now())
                        .build()));
    }

    private IpAddress registerIpAddress(User user, LoginRequestDTO request) {
        return ipAddressRepository.findByIpAddressAndUser_UserId(request.getIpAddress(), user.getUserId())
                .map(ip -> {
                    ip.setLastSeen(LocalDateTime.now());
                    return ipAddressRepository.save(ip);
                })
                .orElseGet(() -> ipAddressRepository.save(IpAddress.builder()
                        .user(user)
                        .ipAddress(request.getIpAddress())
                        .country(request.getCountry())
                        .city(request.getCity())
                        .lastSeen(LocalDateTime.now())
                        .status("NORMAL")
                        .build()));
    }

    private void flagSuspiciousActivity(User user, Device device, IpAddress ip, RiskScoreDTO riskScore) {
        String description = "Auto-detected: " + String.join(", ", riskScore.getRiskFactors());
        SuspiciousActivity activity = SuspiciousActivity.builder()
                .user(user)
                .device(device)
                .ipAddress(ip)
                .activityType("AUTOMATED_RISK_FLAG")
                .riskScore(riskScore.getTotalRiskScore())
                .description(description)
                .resolved(false)
                .build();
        suspiciousActivityRepository.save(activity);
        log.warn("Suspicious activity flagged for user {} - Risk: {} ({})",
                user.getUserId(), riskScore.getTotalRiskScore(), riskScore.getRiskLevel());
    }

    public List<LoginHistory> getLoginHistory(Long userId) {
        return loginHistoryRepository.findByUser_UserIdOrderByLoginTimeDesc(userId);
    }
}