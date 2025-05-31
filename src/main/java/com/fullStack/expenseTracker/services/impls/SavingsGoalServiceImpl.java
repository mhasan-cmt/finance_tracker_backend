package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.SavingsGoalResponseDto;
import com.fullStack.expenseTracker.dto.requests.SavingsContributionRequestDto;
import com.fullStack.expenseTracker.dto.requests.SavingsGoalRequestDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.exceptions.CategoryNotFoundException;
import com.fullStack.expenseTracker.exceptions.TransactionNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.models.*;
import com.fullStack.expenseTracker.repository.SavingsContributionRepository;
import com.fullStack.expenseTracker.repository.SavingsGoalRepository;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.repository.UserRepository;
import com.fullStack.expenseTracker.services.CategoryService;
import com.fullStack.expenseTracker.services.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final SavingsContributionRepository savingsContributionRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final TransactionRepository transactionRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> createSavingsGoal(SavingsGoalRequestDto requestDto)
            throws UserNotFoundException, CategoryNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + requestDto.getUserId()));

            Category category = null;
            if (requestDto.getCategoryId() != null) {
                category = categoryService.getCategoryById(requestDto.getCategoryId());
            }

            SavingsGoal savingsGoal = new SavingsGoal(
                    user,
                    requestDto.getName(),
                    requestDto.getDescription(),
                    requestDto.getTargetAmount(),
                    requestDto.getStartDate(),
                    requestDto.getTargetDate(),
                    category
            );

            savingsGoalRepository.save(savingsGoal);


            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            "Savings goal created successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to create savings goal: " + e.getMessage());
            throw new UserServiceLogicException("Failed to create savings goal: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> updateSavingsGoal(Long goalId, SavingsGoalRequestDto requestDto)
            throws UserNotFoundException, CategoryNotFoundException, UserServiceLogicException {
        try {
            SavingsGoal savingsGoal = savingsGoalRepository.findById(goalId)
                    .orElseThrow(() -> new UserServiceLogicException("Savings goal not found with id: " + goalId));

            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + requestDto.getUserId()));

            Category category = null;
            if (requestDto.getCategoryId() != null) {
                category = categoryService.getCategoryById(requestDto.getCategoryId());
            }

            savingsGoal.setName(requestDto.getName());
            savingsGoal.setDescription(requestDto.getDescription());
            savingsGoal.setTargetAmount(requestDto.getTargetAmount());
            savingsGoal.setStartDate(requestDto.getStartDate());
            savingsGoal.setTargetDate(requestDto.getTargetDate());
            savingsGoal.setCategory(category);

            savingsGoalRepository.save(savingsGoal);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            "Savings goal updated successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to update savings goal: " + e.getMessage());
            throw new UserServiceLogicException("Failed to update savings goal: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getSavingsGoalById(Long goalId)
            throws UserServiceLogicException {
        try {
            SavingsGoal savingsGoal = savingsGoalRepository.findById(goalId)
                    .orElseThrow(() -> new UserServiceLogicException("Savings goal not found with id: " + goalId));

            SavingsGoalResponseDto responseDto = mapToResponseDto(savingsGoal);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            responseDto
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get savings goal: " + e.getMessage());
            throw new UserServiceLogicException("Failed to get savings goal: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllSavingsGoalsByUser(Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            List<SavingsGoal> savingsGoals = savingsGoalRepository.findByUser(user);
            List<SavingsGoalResponseDto> responseDtos = new ArrayList<>();

            for (SavingsGoal savingsGoal : savingsGoals) {
                responseDtos.add(mapToResponseDto(savingsGoal));
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            responseDtos
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get savings goals: " + e.getMessage());
            throw new UserServiceLogicException("Failed to get savings goals: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getActiveSavingsGoalsByUser(Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            List<SavingsGoal> savingsGoals = savingsGoalRepository.findActiveGoalsByUser(user);
            List<SavingsGoalResponseDto> responseDtos = new ArrayList<>();

            for (SavingsGoal savingsGoal : savingsGoals) {
                responseDtos.add(mapToResponseDto(savingsGoal));
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            responseDtos
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get active savings goals: " + e.getMessage());
            throw new UserServiceLogicException("Failed to get active savings goals: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCompletedSavingsGoalsByUser(Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            List<SavingsGoal> savingsGoals = savingsGoalRepository.findByUserAndCompletedOrderByTargetDateAsc(user, true);
            List<SavingsGoalResponseDto> responseDtos = new ArrayList<>();

            for (SavingsGoal savingsGoal : savingsGoals) {
                responseDtos.add(mapToResponseDto(savingsGoal));
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            responseDtos
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get completed savings goals: " + e.getMessage());
            throw new UserServiceLogicException("Failed to get completed savings goals: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deleteSavingsGoal(Long goalId)
            throws UserServiceLogicException {
        try {
            if (!savingsGoalRepository.existsById(goalId)) {
                throw new UserServiceLogicException("Savings goal not found with id: " + goalId);
            }

            savingsGoalRepository.deleteById(goalId);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            "Savings goal deleted successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to delete savings goal: " + e.getMessage());
            throw new UserServiceLogicException("Failed to delete savings goal: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getContributionsByGoalId(Long goalId)
            throws UserServiceLogicException {
        try {
            if (!savingsGoalRepository.existsById(goalId)) {
                throw new UserServiceLogicException("Savings goal not found with id: " + goalId);
            }

            List<SavingsContribution> contributions = savingsContributionRepository.findBySavingsGoalId(goalId);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            contributions
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get contributions: " + e.getMessage());
            throw new UserServiceLogicException("Failed to get contributions: Try again later!");
        }
    }

    private SavingsGoalResponseDto mapToResponseDto(SavingsGoal savingsGoal) {
        SavingsGoalResponseDto responseDto = new SavingsGoalResponseDto();
        responseDto.setGoalId(savingsGoal.getGoalId());
        responseDto.setName(savingsGoal.getName());
        responseDto.setDescription(savingsGoal.getDescription());
        responseDto.setTargetAmount(savingsGoal.getTargetAmount());
        responseDto.setCurrentAmount(savingsGoal.getCurrentAmount());
        responseDto.setProgressPercentage(savingsGoal.getProgressPercentage());
        responseDto.setStartDate(savingsGoal.getStartDate());
        responseDto.setTargetDate(savingsGoal.getTargetDate());
        responseDto.setCompleted(savingsGoal.isCompleted());

        if (savingsGoal.getCategory() != null) {
            responseDto.setCategoryName(savingsGoal.getCategory().getCategoryName());
            responseDto.setCategoryId(savingsGoal.getCategory().getCategoryId());
        }

        // Calculate days remaining and elapsed
        LocalDate now = LocalDate.now();
        if (savingsGoal.getTargetDate() != null) {
            long daysRemaining = ChronoUnit.DAYS.between(now, savingsGoal.getTargetDate());
            responseDto.setDaysRemaining(daysRemaining > 0 ? daysRemaining : 0);
        }

        long daysElapsed = ChronoUnit.DAYS.between(savingsGoal.getStartDate(), now);
        responseDto.setDaysElapsed(daysElapsed > 0 ? daysElapsed : 0);

        return responseDto;
    }
}