package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.SavingsContribution;
import com.fullStack.expenseTracker.models.SavingsGoal;
import com.fullStack.expenseTracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SavingsContributionRepository extends JpaRepository<SavingsContribution, Long> {
    List<SavingsContribution> findBySavingsGoal(SavingsGoal savingsGoal);

    List<SavingsContribution> findByUser(User user);

    @Query("SELECT sc FROM SavingsContribution sc WHERE sc.savingsGoal.goalId = :goalId ORDER BY sc.contributionDate DESC")
    List<SavingsContribution> findBySavingsGoalId(@Param("goalId") Long goalId);

    @Query("SELECT sc FROM SavingsContribution sc WHERE sc.user.id = :userId ORDER BY sc.contributionDate DESC")
    List<SavingsContribution> findByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(sc.amount) FROM SavingsContribution sc WHERE sc.savingsGoal.goalId = :goalId")
    Double getTotalContributionAmountByGoalId(@Param("goalId") Long goalId);

    @Query("SELECT SUM(sc.amount) FROM SavingsContribution sc WHERE sc.user.id = :userId")
    Double getTotalContributionAmountByUserId(@Param("userId") Long userId);

    @Query("SELECT sc FROM SavingsContribution sc WHERE sc.user.id = :userId AND sc.contributionDate BETWEEN :startDate AND :endDate ORDER BY sc.contributionDate DESC")
    List<SavingsContribution> findByUserIdAndDateRange(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
}
