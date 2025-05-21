package com.fullStack.expenseTracker.services.impls;


import com.fullStack.expenseTracker.dto.reponses.PageResponseDto;
import com.fullStack.expenseTracker.dto.requests.UpdateUserRequestDto;
import com.fullStack.expenseTracker.services.NotificationService;
import com.fullStack.expenseTracker.services.UserService;
import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.dto.reponses.UserResponseDto;
import com.fullStack.expenseTracker.exceptions.*;
import com.fullStack.expenseTracker.factories.RoleFactory;
import com.fullStack.expenseTracker.enums.ETransactionType;
import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.repository.TransactionTypeRepository;
import com.fullStack.expenseTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final RoleFactory roleFactory;
    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;

    @Value("${app.user.profile.upload.dir}")
    private String userProfileUploadDir;


    @Override
    @Transactional
    public ResponseEntity<ApiResponseDto<?>> getAllUsers(int pageNumber, int pageSize, String searchKey)
            throws RoleNotFoundException, UserServiceLogicException {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<User> users = userRepository.findAll(pageable, roleFactory.getInstance("user").getId(), searchKey);

        try {
            List<UserResponseDto> userResponseDtoList = new ArrayList<>();

            for (User u : users) {
                userResponseDtoList.add(userToUserResponseDto(u));
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            new PageResponseDto<>(userResponseDtoList, users.getTotalPages(), users.getTotalElements())
                    )
            );
        } catch (Exception e) {
            log.error("Failed to fetch All users: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to fetch All users: Try again later!");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseDto<?>> enableOrDisableUser(long userId)
            throws UserNotFoundException, UserServiceLogicException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found with id " + userId)
        );

        try {
            user.setEnabled(!user.isEnabled());
            User savedUser = userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS, HttpStatus.OK, "User has been updated successfully!"
                    )
            );
        } catch (Exception e) {
            log.error("Failed to enable/disable user: Could not commit JPA transaction. Error: {}", e.getMessage());
            throw new UserServiceLogicException("Failed to update user: Could not commit database transaction. Please try again later.");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseDto<?>> uploadProfileImg(String email, MultipartFile file)
            throws UserServiceLogicException, UserNotFoundException {
        if (existsByEmail(email)) {
            try {
                User user = findByEmail(email);
                String extention = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf("."));
                String newFileName = user.getUsername().concat(extention);
                Path targetLocation = Paths.get(userProfileUploadDir).resolve(newFileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                user.setProfileImgUrl(String.valueOf(targetLocation));
                User savedUser = userRepository.save(user);
                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.CREATED,
                        "Profile image successfully updated!"
                ));
            } catch (Exception e) {
                log.error("Failed to update profile img: Could not commit JPA transaction. Error: {}", e.getMessage());
                throw new UserServiceLogicException("Failed to update profile image: Could not commit database transaction. Please try again later.");
            }
        }

        throw new UserNotFoundException("User not found with email " + email);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseDto<?>> getProfileImg(String email) throws UserNotFoundException, IOException, UserServiceLogicException {
        if (existsByEmail(email)) {
            try {
                User user = findByEmail(email);

                if (user.getProfileImgUrl() != null) {
                    Path profileImgPath = Paths.get(user.getProfileImgUrl());

                    byte[] imageBytes = Files.readAllBytes(profileImgPath);
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            base64Image
                    ));
                } else {
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            null
                    ));
                }

            } catch (Exception e) {
                log.error("Failed to get profile img: {}", e.getMessage());
                throw new UserServiceLogicException("Failed to get profile image: Try again later!");
            }
        }

        throw new UserNotFoundException("User not found with email " + email);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseDto<?>> deleteProfileImg(String email) throws UserServiceLogicException, UserNotFoundException {
        if (existsByEmail(email)) {
            try {
                User user = findByEmail(email);

                File file = new File(user.getProfileImgUrl());
                if (file.exists()) {
                    if (file.delete()) {
                        user.setProfileImgUrl(null);
                        User savedUser = userRepository.save(user);
                        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.OK,
                                "Profile image removed successfully!"
                        ));
                    } else {
                        throw new UserServiceLogicException("Failed to remove profile image: Try again later!");
                    }
                }
            } catch (Exception e) {
                log.error("Failed to delete profile img: Could not commit JPA transaction. Error: {}", e.getMessage());
                throw new UserServiceLogicException("Failed to remove profile image: Could not commit database transaction. Please try again later.");
            }
        }

        throw new UserNotFoundException("User not found with email " + email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email " + email));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> findByUsername(String username) throws UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        userRepository.findByUsername(username).orElse(null)
                )
        );
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponseDto<?>> updateUser(UpdateUserRequestDto updateUserRequestDto)
            throws UserNotFoundException, UserServiceLogicException {
        if (existsByEmail(updateUserRequestDto.email())) {
            try {
                User user = findByEmail(updateUserRequestDto.email());

                // Update user fields if provided in the request
                if (updateUserRequestDto.phone() != null) {
                    user.setPhone(updateUserRequestDto.phone());
                }
                if (updateUserRequestDto.gender() != null) {
                    user.setGender(updateUserRequestDto.gender());
                }
                if (updateUserRequestDto.firstName() != null) {
                    user.setFirstName(updateUserRequestDto.firstName());
                }
                if (updateUserRequestDto.lastName() != null) {
                    user.setLastName(updateUserRequestDto.lastName());
                }
                if (updateUserRequestDto.dateOfBirth() != null) {
                    user.setDateOfBirth(java.sql.Date.valueOf(updateUserRequestDto.dateOfBirth()));
                }
                if (updateUserRequestDto.address() != null) {
                    user.setAddress(updateUserRequestDto.address());
                }

                User savedUser = userRepository.save(user);

                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDto<>(
                        ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        userToUserResponseDto(savedUser)
                ));
            } catch (Exception e) {
                log.error("Failed to update user profile: Could not commit JPA transaction for update user. Error: {}", e.getMessage());
                throw new UserServiceLogicException("Failed to update user profile: Could not commit database transaction. Please try again later.");
            }
        }

        throw new UserNotFoundException("User not found with email " + updateUserRequestDto.email());
    }

    @Transactional
    protected UserResponseDto userToUserResponseDto(User user) {
        Boolean exists = transactionRepository.existsByUser_Id(user.getId());
        Double totalExpenses = 0.0;
        Double totalIncome = 0.0;
        Integer totalTransactions = 0;

        if (exists) {
            int currentMonth = LocalDate.now().getMonthValue();
            int currentYear = LocalDate.now().getYear();

            totalExpenses = transactionRepository.findTotalByUserAndTransactionType(
                    user.getId(),
                    transactionTypeRepository.findByTransactionTypeName(ETransactionType.TYPE_EXPENSE).getTransactionTypeId(),
                    currentMonth,
                    currentYear
            );
            totalIncome = transactionRepository.findTotalByUserAndTransactionType(
                    user.getId(),
                    transactionTypeRepository.findByTransactionTypeName(ETransactionType.TYPE_INCOME).getTransactionTypeId(),
                    currentMonth,
                    currentYear
            );
            totalTransactions = transactionRepository.findTotalNoOfTransactionsByUser(user.getId(), currentMonth, currentYear);
        }

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.isEnabled(),
                totalExpenses != null ? totalExpenses : 0D,
                totalIncome != null ? totalIncome : 0D,
                totalTransactions != null ? totalTransactions : (int) 0L,
                user.getPhone(),
                user.getGender(),
                user.getFirstName(),
                user.getLastName(),
                user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null,
                user.getAddress(),
                user.getProfileImgUrl()
        );
    }


}
