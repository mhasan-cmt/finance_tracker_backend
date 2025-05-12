package com.fullStack.expenseTracker.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsGoalRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Target amount is required")
    @Min(value = 1, message = "Target amount must be greater than 0")
    private Double targetAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate targetDate;

    private Integer categoryId;
}