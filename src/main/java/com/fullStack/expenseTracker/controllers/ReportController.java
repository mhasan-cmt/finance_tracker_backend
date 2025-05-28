package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.security.UserDetailsImpl;
import com.fullStack.expenseTracker.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;


    @GetMapping("/getTotalIncomeOrExpense")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getTotalIncomeOrExpense(@AuthenticationPrincipal UserDetailsImpl user,
                                                                     @Param("transactionTypeId") int transactionTypeId,
                                                                     @Param("month") int month,
                                                                     @Param("year") int year) {
        return reportService.getTotalByTransactionTypeAndUser(user.getId(), transactionTypeId, month, year);
    }

    @GetMapping("/getTotalNoOfTransactions")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getTotalNoOfTransactions(@Param("userId") Long userId,
                                                                      @Param("month") int month,
                                                                      @Param("year") int year) {
        return reportService.getTotalNoOfTransactionsByUser(userId, month, year);
    }

    @GetMapping("/getTotalByCategory")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getTotalByCategory(@Param("email") String email,
                                                                @Param("categoryId") int categoryId,
                                                                @Param("month") int month,
                                                                @Param("year") int year) {
        return reportService.getTotalExpenseByCategoryAndUser(email, categoryId, month, year);
    }

    @GetMapping("/getMonthlySummaryByUser")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByUser(@AuthenticationPrincipal UserDetailsImpl principal) {
        return reportService.getMonthlySummaryByUser(principal.getEmail());
    }


    @GetMapping("/getMonthlySummaryByCategory")
    @PreAuthorize(("hasRole('ROLE_USER')"))
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByCategory(@AuthenticationPrincipal UserDetailsImpl principal) {
        return reportService.getMonthlySummaryByCategory(principal.getId());
    }

}
