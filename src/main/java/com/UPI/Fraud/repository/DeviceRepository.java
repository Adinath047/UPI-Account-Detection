package com.UPI.Fraud.repository;

import com.UPI.Fraud.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUser_UserId(Long userId);
    Optional<Device> findByDeviceHash(String deviceHash);
    boolean existsByDeviceHash(String deviceHash);
    long countByUser_UserId(Long userId);
}