package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.services.TransactionTypeService;
import com.fullStack.expenseTracker.models.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/transactiontype")
@RequiredArgsConstructor
public class TransactionTypeController {


    private final TransactionTypeService transactionTypeService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<TransactionType> getAllTransactionTypes() {
        return transactionTypeService.getAllTransactions();
    }
}
