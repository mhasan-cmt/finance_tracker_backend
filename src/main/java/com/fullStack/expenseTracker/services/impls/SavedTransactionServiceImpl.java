package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.SavedTransactionResponseDto;
import com.fullStack.expenseTracker.dto.requests.SavedTransactionRequestDto;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.enums.ETransactionFrequency;
import com.fullStack.expenseTracker.exceptions.CategoryNotFoundException;
import com.fullStack.expenseTracker.exceptions.TransactionNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserNotFoundException;
import com.fullStack.expenseTracker.exceptions.UserServiceLogicException;
import com.fullStack.expenseTracker.models.SavedTransaction;
import com.fullStack.expenseTracker.models.Transaction;
import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.repository.SavedTransactionRepository;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.repository.UserRepository;
import com.fullStack.expenseTracker.services.CategoryService;
import com.fullStack.expenseTracker.services.SavedTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class SavedTransactionServiceImpl implements SavedTransactionService {
    private final UserRepository userRepository;
    private final SavedTransactionRepository savedTransactionRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;


    @Override
    public ResponseEntity<ApiResponseDto<?>> createSavedTransaction(SavedTransactionRequestDto requestDto)
            throws UserServiceLogicException, UserNotFoundException {
        try {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + requestDto.getUserId()));

            SavedTransaction plannedTransaction = savedTransactionDtoToEntity(requestDto);
            plannedTransaction = savedTransactionRepository.save(plannedTransaction);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.CREATED,
                            "Transaction has been successfully created!"
                    )
            );
        }catch(Exception e) {
            throw new UserServiceLogicException("Failed to create transaction. Try again later");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> addSavedTransaction(long savedTransactionId)
            throws UserServiceLogicException, TransactionNotFoundException {
        try {
            if (savedTransactionRepository.existsById(savedTransactionId)) {
                SavedTransaction plannedTransaction = savedTransactionRepository.findById(savedTransactionId)
                        .orElse(null);

                transactionRepository.save(savedTransactionToTransaction(plannedTransaction));

                LocalDate upcomingDate = getUpcomingDate(plannedTransaction.getFrequency(), plannedTransaction.getUpcomingDate());

                plannedTransaction.setUpcomingDate(upcomingDate);
                savedTransactionRepository.save(plannedTransaction);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.CREATED,
                                "Transaction has been successfully saved!"
                        )
                );
            }
        }catch(Exception e) {
            throw new UserServiceLogicException("Failed to add transaction. Try again later");
        }
        throw new TransactionNotFoundException("Transaction not found with id: " + savedTransactionId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> editSavedTransaction(long plannedTransactionId, SavedTransactionRequestDto requestDto)
            throws UserServiceLogicException, TransactionNotFoundException {
        try {
            if (savedTransactionRepository.existsById(plannedTransactionId)) {
                SavedTransaction plannedTransaction = savedTransactionRepository.findById(plannedTransactionId)
                        .orElse(null);

                plannedTransaction.setTransactionType(categoryService.getCategoryById(requestDto.getCategoryId()).getTransactionType());
                plannedTransaction.setCategory(categoryService.getCategoryById(requestDto.getCategoryId()));
                plannedTransaction.setUser(userRepository.findById(requestDto.getUserId())
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + requestDto.getUserId())));
                plannedTransaction.setAmount(requestDto.getAmount());
                plannedTransaction.setDescription(requestDto.getDescription());
                plannedTransaction.setFrequency(requestDto.getFrequency());
                plannedTransaction.setUpcomingDate(requestDto.getUpcomingDate());

                plannedTransaction = savedTransactionRepository.save(plannedTransaction);

                return ResponseEntity.status(HttpStatus.OK).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.OK,
                                "Transaction has been successfully edited!"
                        )
                );
            }
        }catch(Exception e) {
            throw new UserServiceLogicException("Failed to edit transaction. Try again later");
        }
        throw new TransactionNotFoundException("Transaction not found with id: " + plannedTransactionId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> deleteSavedTransaction(long plannedTransactionId)
            throws UserServiceLogicException, TransactionNotFoundException {
        try {
            if (savedTransactionRepository.existsById(plannedTransactionId)) {

                savedTransactionRepository.deleteById(plannedTransactionId);

                return ResponseEntity.status(HttpStatus.OK).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.OK,
                                "Transaction deleted successfully!"
                        )
                );
            }
        }catch(Exception e) {
            throw new UserServiceLogicException("Failed to delete transaction. Try again later.");
        }
        throw new TransactionNotFoundException("Transaction not found with id: " + plannedTransactionId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> skipSavedTransaction(long savedTransactionId) throws UserServiceLogicException, TransactionNotFoundException {
        try {
            if (savedTransactionRepository.existsById(savedTransactionId)) {
                SavedTransaction plannedTransaction = savedTransactionRepository.findById(savedTransactionId)
                        .orElse(null);

                LocalDate upcomingDate = getUpcomingDate(plannedTransaction.getFrequency(), plannedTransaction.getUpcomingDate());

                plannedTransaction.setUpcomingDate(upcomingDate);
                savedTransactionRepository.save(plannedTransaction);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.CREATED,
                                "Transaction has been successfully skipped for period!"
                        )
                );
            }
        }catch(Exception e) {
            throw new UserServiceLogicException("Failed to add transaction. Try again later");
        }
        throw new TransactionNotFoundException("Transaction not found with id: " + savedTransactionId);
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllTransactionsByUser(long userId) throws UserServiceLogicException, UserNotFoundException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            List<SavedTransaction> transactions = savedTransactionRepository.findByUserOrderByUpcomingDateAsc(user);

            List<SavedTransactionResponseDto> response = new ArrayList<>();

            for (SavedTransaction t: transactions) {
                response.add(savedTransactionToDto(t));
            }
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            response
                    )
            );
        }catch(Exception e) {
            log.error(e.getMessage());
            throw new UserServiceLogicException("Failed to fetch transactions. Try again later");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getAllTransactionsByUserAndMonth(long userId) throws UserServiceLogicException, UserNotFoundException {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            List<SavedTransaction> transactions = savedTransactionRepository.findByUserOrderByUpcomingDateAsc(user).stream()
                    .filter(t -> t.getUpcomingDate().getMonthValue() == LocalDate.now().getMonthValue())
                    .toList();

            List<SavedTransactionResponseDto> response = new ArrayList<>();

            for (SavedTransaction t: transactions) {
                response.add(savedTransactionToDto(t));
            }

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponseDto<>(
                            ApiResponseStatus.SUCCESS,
                            HttpStatus.OK,
                            response
                    )
            );
        }catch(Exception e) {
            throw new UserServiceLogicException("Failed to fetch transactions. Try again later");
        }
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getSavedTransactionById(long savedTransactionId)
            throws UserServiceLogicException, TransactionNotFoundException {
        try {
            if (savedTransactionRepository.existsById(savedTransactionId)) {
                SavedTransaction plannedTransaction = savedTransactionRepository.findById(savedTransactionId)
                        .orElse(null);


                return ResponseEntity.status(HttpStatus.OK).body(
                        new ApiResponseDto<>(
                                ApiResponseStatus.SUCCESS,
                                HttpStatus.OK,
                                plannedTransaction
                        )
                );
            }
        }catch(Exception e) {
            throw new UserServiceLogicException("Failed to fetch transaction. Try again later.");
        }
        throw new TransactionNotFoundException("Transaction not found with id: " + savedTransactionId);
    }

    private SavedTransaction savedTransactionDtoToEntity(SavedTransactionRequestDto requestDto) throws CategoryNotFoundException, UserNotFoundException {
        return SavedTransaction.builder()
                .transactionType(categoryService.getCategoryById(requestDto.getCategoryId()).getTransactionType())
                .category(categoryService.getCategoryById(requestDto.getCategoryId()))
                .user(userRepository.findById(requestDto.getUserId())
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + requestDto.getUserId())))
                .amount(requestDto.getAmount())
                .description(requestDto.getDescription())
                .upcomingDate(requestDto.getUpcomingDate())
                .frequency(requestDto.getFrequency())
                .build();
    }

    private Transaction savedTransactionToTransaction(SavedTransaction savedTransaction) {
        return new Transaction(
                savedTransaction.getUser(),
                savedTransaction.getCategory(),
                savedTransaction.getDescription(),
                savedTransaction.getAmount(),
                savedTransaction.getUpcomingDate()
        );
    }

    private LocalDate getUpcomingDate(ETransactionFrequency frequency, LocalDate currentDate) {
        if (frequency == ETransactionFrequency.DAILY) {
            return currentDate.plusDays(1);
        }
        if (frequency == ETransactionFrequency.MONTHLY) {
            return currentDate.plusMonths(1);
        }
        return null;
    }

    private SavedTransactionResponseDto savedTransactionToDto(SavedTransaction savedTransaction) {
        return new SavedTransactionResponseDto(
                savedTransaction.getPlanId(),
                savedTransaction.getTransactionType().getTransactionTypeId(),
                savedTransaction.getCategory().getCategoryName(),
                savedTransaction.getAmount(),
                savedTransaction.getDescription(),
                savedTransaction.getFrequency(),
                getDueInformation(savedTransaction)
        );
    }

    private String getDueInformation(SavedTransaction transaction) {
        if (transaction.getUpcomingDate() == null) return null;
        if (Objects.equals(transaction.getUpcomingDate(), LocalDate.now()))
            return "Due on Today";
        if (Objects.equals(transaction.getUpcomingDate(), LocalDate.now().plusDays(1)))
            return "Due on Tomorrow";
        if (Objects.equals(transaction.getUpcomingDate(), LocalDate.now().plusDays(2)))
            return "Due on a day after tomorrow";
        if (transaction.getFrequency() == ETransactionFrequency.MONTHLY && transaction.getUpcomingDate().isBefore(LocalDate.now())) {
            Period period = Period.between(transaction.getUpcomingDate(), LocalDate.now());
            if (period.getMonths() >= 0 && period.getDays() > 0)
                return period.getMonths() + 1 + " Months over due";
            return period.getMonths() + " Months over due";
        }
        if (Objects.equals(transaction.getUpcomingDate(), LocalDate.now().minusDays(1)))
            return "1 day overdue";
        if (transaction.getUpcomingDate().isBefore(LocalDate.now())){
            Period period = Period.between(transaction.getUpcomingDate(), LocalDate.now());
            long days =period.getYears()* 365L + period.getMonths()* 30L + period.getDays();
            return days + " days overdue";
        }
        return "Due on " + transaction.getUpcomingDate();

    }
}
