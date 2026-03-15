package com.UPI.Fraud.controller;

import com.UPI.Fraud.DTOs.LoginRequestDTO;
import com.UPI.Fraud.DTOs.RiskScoreDTO;
import com.UPI.Fraud.entities.LoginHistory;
import com.UPI.Fraud.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LoginController {

    private final LoginService loginService;

    /**
     * Process a login attempt and return real-time risk score
     */
    @PostMapping
    public ResponseEntity<RiskScoreDTO> login(@RequestBody LoginRequestDTO request) {
        RiskScoreDTO riskScore = loginService.processLogin(request);
        return ResponseEntity.ok(riskScore);
    }

    /**
     * Get full login history for a user
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<LoginHistory>> getLoginHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(loginService.getLoginHistory(userId));
    }
}