package com.UPI.Fraud.repository;

import com.UPI.Fraud.entities.SuspiciousActivity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuspiciousActivityRepository extends JpaRepository<SuspiciousActivity, Long> {
    List<SuspiciousActivity> findByUser_UserId(Long userId);
    List<SuspiciousActivity> findByResolved(Boolean resolved);
    List<SuspiciousActivity> findByRiskScoreGreaterThanEqual(Integer threshold);

    @Query("SELECT sa FROM SuspiciousActivity sa WHERE sa.user.userId = :userId ORDER BY sa.detectedAt DESC")
    List<SuspiciousActivity> findRecentByUser(@Param("userId") Long userId);

    @Query("SELECT sa FROM SuspiciousActivity sa WHERE sa.resolved = false ORDER BY sa.riskScore DESC")
    List<SuspiciousActivity> findAllUnresolved();

    @Query("SELECT AVG(sa.riskScore) FROM SuspiciousActivity sa WHERE sa.user.userId = :userId")
    Double avgRiskScoreForUser(@Param("userId") Long userId);
}