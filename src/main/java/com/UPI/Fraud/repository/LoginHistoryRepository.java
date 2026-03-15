package com.UPI.Fraud.repository;

import com.UPI.Fraud.entities.LoginHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findByUser_UserId(Long userId);
    List<LoginHistory> findByUser_UserIdOrderByLoginTimeDesc(Long userId);

    @Query("SELECT COUNT(l) FROM LoginHistory l WHERE l.user.userId = :userId AND l.loginTime >= :since")
    long countLoginsAfter(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT l.ipAddress.ipId) FROM LoginHistory l WHERE l.user.userId = :userId AND l.loginTime >= :since")
    long countDistinctIpsAfter(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(DISTINCT l.device.deviceId) FROM LoginHistory l WHERE l.user.userId = :userId AND l.loginTime >= :since")
    long countDistinctDevicesAfter(@Param("userId") Long userId, @Param("since") LocalDateTime since);
}