package com.fullStack.expenseTracker.dto.reponses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavingsGoalResponseDto {
    private Long goalId;
    private String name;
    private String description;
    private Double targetAmount;
    private Double currentAmount;
    private Double progressPercentage;
    private LocalDate startDate;
    private LocalDate targetDate;
    private boolean completed;
    private String categoryName;
    private Integer categoryId;
    private Long daysRemaining;
    private Long daysElapsed;
}