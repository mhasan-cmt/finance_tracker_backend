package com.fullStack.expenseTracker.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsContributionRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Goal ID is required")
    private Long goalId;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amount;

    private Long transactionId;
}