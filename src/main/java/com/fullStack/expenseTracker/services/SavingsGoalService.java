package com.fullStack.expenseTracker.services;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.SavingsContributionRequestDto;
import com.fullStack.expenseTracker.dto.requests.SavingsGoalRequestDto;
import com.fullStack.expenseTracker.exceptions.CategoryNotFoundException;
import com.fullStack.expenseTracker.exceptions.TransactionNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SavingsGoalService {
    ResponseEntity<ApiResponseDto<?>> createSavingsGoal(SavingsGoalRequestDto requestDto) 
            throws UserNotFoundException, CategoryNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> updateSavingsGoal(Long goalId, SavingsGoalRequestDto requestDto) 
            throws UserNotFoundException, CategoryNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getSavingsGoalById(Long goalId) 
            throws UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getAllSavingsGoalsByUser(Long userId) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getActiveSavingsGoalsByUser(Long userId) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> getCompletedSavingsGoalsByUser(Long userId) 
            throws UserNotFoundException, UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> deleteSavingsGoal(Long goalId) 
            throws UserServiceLogicException;
    
    ResponseEntity<ApiResponseDto<?>> addContribution(SavingsContributionRequestDto requestDto) 
            throws UserNotFoundException, UserServiceLogicException, TransactionNotFoundException;
    
    ResponseEntity<ApiResponseDto<?>> getContributionsByGoalId(Long goalId) 
            throws UserServiceLogicException;
}