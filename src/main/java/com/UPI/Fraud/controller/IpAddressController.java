package com.UPI.Fraud.controller;

import com.UPI.Fraud.entities.IpAddress;
import com.UPI.Fraud.repository.IpAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ip-addresses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IpAddressController {

    private final IpAddressRepository ipAddressRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IpAddress>> getIpsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ipAddressRepository.findByUser_UserId(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<IpAddress>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(ipAddressRepository.findByStatus(status));
    }

    /**
     * Update IP status: NORMAL, SUSPICIOUS, BLACKLISTED
     */
    @PatchMapping("/{ipId}/status")
    public ResponseEntity<IpAddress> updateStatus(@PathVariable Long ipId,
                                                  @RequestParam String status) {
        IpAddress ip = ipAddressRepository.findById(ipId)
                .orElseThrow(() -> new RuntimeException("IP not found: " + ipId));
        ip.setStatus(status);
        return ResponseEntity.ok(ipAddressRepository.save(ip));
    }
}