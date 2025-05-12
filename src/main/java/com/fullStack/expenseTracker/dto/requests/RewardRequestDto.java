package com.fullStack.expenseTracker.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardRequestDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Points required is required")
    @Min(value = 1, message = "Points required must be greater than 0")
    private Integer pointsRequired;
}