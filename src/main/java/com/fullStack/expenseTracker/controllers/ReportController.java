package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.security.UserDetailsImpl;
import com.fullStack.expenseTracker.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/mywallet/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/getTotalIncomeOrExpense")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getTotalIncomeOrExpense(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam int transactionTypeId,
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.getTotalByTransactionTypeAndUser(user.getId(), transactionTypeId, month, year);
    }

    @GetMapping("/getTotalNoOfTransactions")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getTotalNoOfTransactions(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.getTotalNoOfTransactionsByUser(user.getId(), month, year);
    }

    @GetMapping("/getTotalByCategory")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getTotalByCategory(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam int categoryId,
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.getTotalExpenseByCategoryAndUser(user.getEmail(), categoryId, month, year);
    }

    @GetMapping("/getMonthlySummaryByUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByUser(
            @AuthenticationPrincipal UserDetailsImpl user) {
        return reportService.getMonthlySummaryByUser(user.getId());
    }

    @GetMapping("/getMonthlySummaryByCategory")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByCategory(
            @AuthenticationPrincipal UserDetailsImpl user) {
        return reportService.getMonthlySummaryByCategory(user.getId());
    }

    @GetMapping("/chart/monthly-income-expense")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getMonthlyIncomeExpenseChart(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam int year) {
        return reportService.getMonthlyIncomeExpenseChartData(user.getId(), year);
    }

    @GetMapping("/chart/category-breakdown")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getCategoryBreakdownChart(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.getCategoryBreakdownChartData(user.getId(), month, year);
    }

    @GetMapping("/chart/daily-expense")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponseDto<?>> getDailyExpenseChart(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestParam int month,
            @RequestParam int year) {
        return reportService.getDailyExpenseChartData(user.getId(), month, year);
    }
}
