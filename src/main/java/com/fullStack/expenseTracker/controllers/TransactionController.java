package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.exceptions.*;
import com.fullStack.expenseTracker.security.UserDetailsImpl;
import com.fullStack.expenseTracker.services.TransactionService;
import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.requests.TransactionRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/transaction")
@RequiredArgsConstructor
public class TransactionController {


    private final TransactionService transactionService;

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<?>> getAllTransactions(@Param("pageNumber") int pageNumber,
                                                         @Param("pageSize") int pageSize,
                                                         @Param("searchKey") String searchKey) throws TransactionServiceLogicException {
        return transactionService.getAllTransactions(pageNumber, pageSize, searchKey);
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> addTransaction(@RequestBody @Valid TransactionRequestDto transactionRequestDto, @AuthenticationPrincipal UserDetailsImpl principal)
            throws UserNotFoundException, CategoryNotFoundException, TransactionServiceLogicException {
        transactionRequestDto.setUserEmail(principal.getEmail());
        return transactionService.addTransaction(transactionRequestDto);
    }

    @GetMapping("/getByUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getTransactionsByUser(@AuthenticationPrincipal UserDetailsImpl principal,
                                                                   @Param("pageNumber") int pageNumber,
                                                                   @Param("pageSize") int pageSize,
                                                                   @Param("searchKey") String searchKey,
                                                                   @Param("sortField") String sortField,
                                                                   @Param("sortDirec") String sortDirec,
                                                                   @Param("transactionType") String transactionType)
            throws UserNotFoundException, TransactionServiceLogicException {
        log.info("Fetching transactions for user: {}", principal.getEmail());

        return transactionService.getTransactionsByUser(principal.getEmail(), pageNumber, pageSize, searchKey, sortField, sortDirec, transactionType);
    }

    @GetMapping("/getById")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getTransactionById(@Param("id") Long id)
            throws TransactionNotFoundException {

        return transactionService.getTransactionById(id);

    }


    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> updateTransaction(@Param("transactionId") Long transactionId,
                                                               @RequestBody @Valid TransactionRequestDto transactionRequestDto,
                                                               @AuthenticationPrincipal UserDetailsImpl principal)
            throws UserNotFoundException, CategoryNotFoundException, TransactionNotFoundException, TransactionServiceLogicException {
        transactionRequestDto.setUserEmail(principal.getEmail());
        return transactionService.updateTransaction(transactionId, transactionRequestDto);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> deleteTransaction(@Param("transactionId") Long transactionId)
            throws TransactionNotFoundException, TransactionServiceLogicException {

        return transactionService.deleteTransaction(transactionId);

    }

}
