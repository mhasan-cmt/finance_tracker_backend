package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.SavingsGoal;
import com.fullStack.expenseTracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
    
    List<SavingsGoal> findByUserAndCompletedOrderByTargetDateAsc(User user, boolean completed);
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user = :user AND sg.completed = false ORDER BY sg.targetDate ASC")
    List<SavingsGoal> findActiveGoalsByUser(@Param("user") User user);
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user.id = :userId AND sg.completed = false ORDER BY sg.targetDate ASC")
    List<SavingsGoal> findActiveGoalsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user.id = :userId AND sg.completed = true ORDER BY sg.targetDate DESC")
    List<SavingsGoal> findCompletedGoalsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(sg) FROM SavingsGoal sg WHERE sg.user.id = :userId AND sg.completed = true")
    int countCompletedGoalsByUserId(@Param("userId") Long userId);
}