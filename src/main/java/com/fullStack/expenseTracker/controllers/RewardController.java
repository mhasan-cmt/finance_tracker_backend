package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.RewardRequestDto;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.services.RewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> createReward(@RequestBody @Valid RewardRequestDto requestDto)
            throws UserNotFoundException, UserServiceLogicException {
        return rewardService.createReward(requestDto);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> updateReward(
            @Param("rewardId") Long rewardId,
            @RequestBody @Valid RewardRequestDto requestDto)
            throws UserNotFoundException, UserServiceLogicException {
        return rewardService.updateReward(rewardId, requestDto);
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getRewardById(@Param("rewardId") Long rewardId)
            throws UserServiceLogicException {
        return rewardService.getRewardById(rewardId);
    }

    @GetMapping("/user/all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getAllRewardsByUser(@Param("userId") Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return rewardService.getAllRewardsByUser(userId);
    }

    @GetMapping("/user/available")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getAvailableRewardsByUser(@Param("userId") Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return rewardService.getAvailableRewardsByUser(userId);
    }

    @GetMapping("/user/redeemed")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getRedeemedRewardsByUser(@Param("userId") Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return rewardService.getRedeemedRewardsByUser(userId);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> deleteReward(@Param("rewardId") Long rewardId)
            throws UserServiceLogicException {
        return rewardService.deleteReward(rewardId);
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> redeemReward(
            @Param("rewardId") Long rewardId,
            @Param("userId") Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return rewardService.redeemReward(rewardId, userId);
    }

    @GetMapping("/points")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getUserPoints(@Param("userId") Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return rewardService.getUserPoints(userId);
    }

    @PostMapping("/points/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> addUserPoints(
            @Param("userId") Long userId,
            @Param("points") Integer points)
            throws UserNotFoundException, UserServiceLogicException {
        return rewardService.addUserPoints(userId, points);
    }
}