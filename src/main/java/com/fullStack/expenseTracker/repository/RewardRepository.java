package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.Reward;
import com.fullStack.expenseTracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByUser(User user);
    
    List<Reward> findByUserAndIsRedeemedOrderByPointsRequiredAsc(User user, boolean isRedeemed);
    
    @Query("SELECT r FROM Reward r WHERE r.user = :user AND r.isRedeemed = false ORDER BY r.pointsRequired ASC")
    List<Reward> findAvailableRewardsByUser(@Param("user") User user);
    
    @Query("SELECT r FROM Reward r WHERE r.user.id = :userId AND r.isRedeemed = false ORDER BY r.pointsRequired ASC")
    List<Reward> findAvailableRewardsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Reward r WHERE r.user.id = :userId AND r.isRedeemed = true ORDER BY r.redeemedDate DESC")
    List<Reward> findRedeemedRewardsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Reward r WHERE r.user.id = :userId AND r.isRedeemed = true")
    int countRedeemedRewardsByUserId(@Param("userId") Long userId);
}