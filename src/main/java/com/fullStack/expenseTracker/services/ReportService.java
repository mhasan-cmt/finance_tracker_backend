package com.fullStack.expenseTracker.services;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ReportService {
    ResponseEntity<ApiResponseDto<?>> getTotalByTransactionTypeAndUser(Long userId, int transactionTypeId, int month, int year);

    ResponseEntity<ApiResponseDto<?>> getTotalNoOfTransactionsByUser(Long userId, int month, int year);

    ResponseEntity<ApiResponseDto<?>> getTotalExpenseByCategoryAndUser(String email, int categoryId, int month, int year);

    ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByUser(Long userID);

    ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByCategory(Long userId);

    // ✅ Chart: Monthly income vs expense for a year (e.g., line chart)
    ResponseEntity<ApiResponseDto<?>> getMonthlyIncomeExpenseChartData(Long userId, int year);

    // ✅ Chart: Category-wise breakdown for pie chart
    ResponseEntity<ApiResponseDto<?>> getCategoryBreakdownChartData(Long userId, int month, int year);

    // ✅ Chart: Daily income and expense in a specific month for bar chart
    ResponseEntity<ApiResponseDto<?>> getDailyIncomeExpenseChartData(Long userId, int month, int year);

    ResponseEntity<ApiResponseDto<?>> getYearlyIncomeExpenseChartData(Long id, int year);
}


