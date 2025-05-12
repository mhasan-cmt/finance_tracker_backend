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
public class RewardResponseDto {
    private Long rewardId;
    private String name;
    private String description;
    private Integer pointsRequired;
    private boolean isRedeemed;
    private LocalDate createdDate;
    private LocalDate redeemedDate;
    private boolean canRedeem;
}