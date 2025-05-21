package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.RewardResponseDto;
import com.fullStack.expenseTracker.dto.requests.RewardRequestDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.models.Reward;
import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.models.UserPoints;
import com.fullStack.expenseTracker.repository.RewardRepository;
import com.fullStack.expenseTracker.repository.UserPointsRepository;
import com.fullStack.expenseTracker.repository.UserRepository;
import com.fullStack.expenseTracker.services.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final UserPointsRepository userPointsRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> createReward(RewardRequestDto requestDto)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + requestDto.getUserId()));

            Reward reward = new Reward(
                    user,
                    requestDto.getName(),
                    requestDto.getDescription(),
                    requestDto.getPointsRequired()
            );

            rewardRepository.save(reward);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            "Reward created successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to create reward: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to create reward: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> updateReward(Long rewardId, RewardRequestDto requestDto)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            Reward reward = rewardRepository.findById(rewardId)
                    .orElseThrow(() -> new UserServiceLogicException("Reward not found with id: " + rewardId));

            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + requestDto.getUserId()));

            reward.setName(requestDto.getName());
            reward.setDescription(requestDto.getDescription());
            reward.setPointsRequired(requestDto.getPointsRequired());

            rewardRepository.save(reward);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            "Reward updated successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to update reward: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to update reward: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getRewardById(Long rewardId)
            throws UserServiceLogicException {
        try {
            Reward reward = rewardRepository.findById(rewardId)
                    .orElseThrow(() -> new UserServiceLogicException("Reward not found with id: " + rewardId));

            RewardResponseDto responseDto = mapToResponseDto(reward);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            responseDto
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get reward: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to get reward: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllRewardsByUser(Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            List<Reward> rewards = rewardRepository.findByUser(user);
            List<RewardResponseDto> responseDtos = new ArrayList<>();

            int userPoints = getUserPointsValue(userId);

            for (Reward reward : rewards) {
                RewardResponseDto dto = mapToResponseDto(reward);
                dto.setCanRedeem(!reward.isRedeemed() && userPoints >= reward.getPointsRequired());
                responseDtos.add(dto);
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            responseDtos
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get rewards: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to get rewards: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAvailableRewardsByUser(Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            List<Reward> rewards = rewardRepository.findAvailableRewardsByUser(user);
            List<RewardResponseDto> responseDtos = new ArrayList<>();

            int userPoints = getUserPointsValue(userId);

            for (Reward reward : rewards) {
                RewardResponseDto dto = mapToResponseDto(reward);
                dto.setCanRedeem(userPoints >= reward.getPointsRequired());
                responseDtos.add(dto);
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            responseDtos
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get available rewards: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to get available rewards: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getRedeemedRewardsByUser(Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            List<Reward> rewards = rewardRepository.findByUserAndIsRedeemedOrderByPointsRequiredAsc(user, true);
            List<RewardResponseDto> responseDtos = new ArrayList<>();

            for (Reward reward : rewards) {
                responseDtos.add(mapToResponseDto(reward));
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            responseDtos
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get redeemed rewards: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to get redeemed rewards: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deleteReward(Long rewardId)
            throws UserServiceLogicException {
        try {
            if (!rewardRepository.existsById(rewardId)) {
                throw new UserServiceLogicException("Reward not found with id: " + rewardId);
            }

            rewardRepository.deleteById(rewardId);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            "Reward deleted successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to delete reward: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to delete reward: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> redeemReward(Long rewardId, Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            Reward reward = rewardRepository.findById(rewardId)
                    .orElseThrow(() -> new UserServiceLogicException("Reward not found with id: " + rewardId));

            if (reward.isRedeemed()) {
                throw new UserServiceLogicException("Reward has already been redeemed!");
            }

            UserPoints userPoints = getUserPointsEntity(userId);
            if (userPoints.getPoints() < reward.getPointsRequired()) {
                throw new UserServiceLogicException("Not enough points to redeem this reward!");
            }

            // Deduct points
            userPoints.usePoints(reward.getPointsRequired());
            userPointsRepository.save(userPoints);

            // Mark reward as redeemed
            reward.redeem();
            rewardRepository.save(reward);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            "Reward redeemed successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to redeem reward: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to redeem reward: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getUserPoints(Long userId)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            UserPoints userPoints = getUserPointsEntity(userId);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            userPoints.getPoints()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to get user points: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to get user points: Try again later!");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> addUserPoints(Long userId, Integer points)
            throws UserNotFoundException, UserServiceLogicException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            UserPoints userPoints = getUserPointsEntity(userId);
            userPoints.addPoints(points);
            userPointsRepository.save(userPoints);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            userPoints.getPoints()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to add user points: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to add user points: Try again later!");
        }
    }

    private UserPoints getUserPointsEntity(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Optional<UserPoints> userPointsOpt = userPointsRepository.findByUser(user);
        if (userPointsOpt.isPresent()) {
            return userPointsOpt.get();
        } else {
            UserPoints newUserPoints = new UserPoints(user);
            return userPointsRepository.save(newUserPoints);
        }
    }

    private Integer getUserPointsValue(Long userId) {
        try {
            Integer points = userPointsRepository.findPointsByUserId(userId);
            return points != null ? points : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private RewardResponseDto mapToResponseDto(Reward reward) {
        return RewardResponseDto.builder()
                .rewardId(reward.getRewardId())
                .name(reward.getName())
                .description(reward.getDescription())
                .pointsRequired(reward.getPointsRequired())
                .isRedeemed(reward.isRedeemed())
                .createdDate(reward.getCreatedDate())
                .redeemedDate(reward.getRedeemedDate())
                .canRedeem(false) // Will be set based on user points
                .build();
    }
}
