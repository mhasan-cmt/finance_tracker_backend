package com.fullStack.expenseTracker.services;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.RewardRequestDto;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface RewardService {
    ResponseEntity<ApiResponseDto<?>> createReward(RewardRequestDto requestDto) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> updateReward(Long rewardId, RewardRequestDto requestDto) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getRewardById(Long rewardId) 
            throws UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getAllRewardsByUser(Long userId) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getAvailableRewardsByUser(Long userId) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getRedeemedRewardsByUser(Long userId) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> deleteReward(Long rewardId) 
            throws UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> redeemReward(Long rewardId, Long userId) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getUserPoints(Long userId) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> addUserPoints(Long userId, Integer points) 
            throws UserNotFoundException, UserServiceLogicException;
}