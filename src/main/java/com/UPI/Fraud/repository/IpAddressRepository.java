package com.UPI.Fraud.repository;

import com.UPI.Fraud.entities.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IpAddressRepository extends JpaRepository<IpAddress, Long> {
    List<IpAddress> findByUser_UserId(Long userId);
    Optional<IpAddress> findByIpAddressAndUser_UserId(String ipAddress, Long userId);
    List<IpAddress> findByStatus(String status);
    long countByUser_UserId(Long userId);
    boolean existsByIpAddressAndUser_UserId(String ipAddress, Long userId);
}