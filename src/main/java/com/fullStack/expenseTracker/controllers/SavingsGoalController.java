package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.SavingsContributionRequestDto;
import com.fullStack.expenseTracker.dto.requests.SavingsGoalRequestDto;
import com.fullStack.expenseTracker.exceptions.CategoryNotFoundException;
import com.fullStack.expenseTracker.exceptions.TransactionNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.services.SavingsGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/savings")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @PostMapping("/goal/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> createSavingsGoal(@RequestBody @Valid SavingsGoalRequestDto requestDto)
            throws UserNotFoundException, CategoryNotFoundException, UserServiceLogicException {
        return savingsGoalService.createSavingsGoal(requestDto);
    }

    @PutMapping("/goal/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> updateSavingsGoal(
            @Param("goalId") Long goalId,
            @RequestBody @Valid SavingsGoalRequestDto requestDto)
            throws UserNotFoundException, CategoryNotFoundException, UserServiceLogicException {
        return savingsGoalService.updateSavingsGoal(goalId, requestDto);
    }

    @GetMapping("/goal")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getSavingsGoalById(@Param("goalId") Long goalId)
            throws UserServiceLogicException {
        return savingsGoalService.getSavingsGoalById(goalId);
    }

    @GetMapping("/goal/user/all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getAllSavingsGoalsByUser(@Param("userId") Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return savingsGoalService.getAllSavingsGoalsByUser(userId);
    }

    @GetMapping("/goal/user/active")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getActiveSavingsGoalsByUser(@Param("userId") Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return savingsGoalService.getActiveSavingsGoalsByUser(userId);
    }

    @GetMapping("/goal/user/completed")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getCompletedSavingsGoalsByUser(@Param("userId") Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        return savingsGoalService.getCompletedSavingsGoalsByUser(userId);
    }

    @DeleteMapping("/goal/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> deleteSavingsGoal(@Param("goalId") Long goalId)
            throws UserServiceLogicException {
        return savingsGoalService.deleteSavingsGoal(goalId);
    }

    @PostMapping("/contribution/add")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> addContribution(@RequestBody @Valid SavingsContributionRequestDto requestDto)
            throws UserNotFoundException, UserServiceLogicException, TransactionNotFoundException {
        return savingsGoalService.addContribution(requestDto);
    }

    @GetMapping("/contribution/goal")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getContributionsByGoalId(@Param("goalId") Long goalId)
            throws UserServiceLogicException {
        return savingsGoalService.getContributionsByGoalId(goalId);
    }
}