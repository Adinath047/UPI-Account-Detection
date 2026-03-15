package com.UPI.Fraud.controller;

import com.UPI.Fraud.entities.Device;
import com.UPI.Fraud.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceRepository deviceRepository;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Device>> getDevicesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(deviceRepository.findByUser_UserId(userId));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<Device> getDevice(@PathVariable Long deviceId) {
        return deviceRepository.findById(deviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long deviceId) {
        deviceRepository.deleteById(deviceId);
        return ResponseEntity.noContent().build();
    }
}