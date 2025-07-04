package com.fullStack.expenseTracker.services.impls;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.TransactionSummaryByCategory;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.dto.reponses.TransactionsMonthlySummaryDto;
import com.fullStack.expenseTracker.enums.ETransactionType;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.services.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;

    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalByTransactionTypeAndUser(Long userId, int transactionTypeId, int month, int year) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionRepository.findTotalByUserAndTransactionType(userId, transactionTypeId, month, year)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalNoOfTransactionsByUser(Long userId, int month, int year) {
        List<Object[]> counts = transactionRepository.findTransactionCountsByType(userId, month, year);

        Map<String, Integer> result = new HashMap<>();
        result.put("income", 0);
        result.put("expense", 0);

        for (Object[] row : counts) {
            Integer typeId = (Integer) row[0];
            long count = ((Number) row[1]).longValue();

            if (typeId == 1) result.put("income", (int) count);
            else if (typeId == 2) result.put("expense", (int) count);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS, HttpStatus.OK, result)
        );
    }


    @Override
    public ResponseEntity<ApiResponseDto<?>> getTotalExpenseByCategoryAndUser(String email, int categoryId, int month, int year) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionRepository.findTotalByUserAndCategory(email, categoryId, month, year)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByUser(Long userID) {
        List<Object[]> result = transactionRepository.findMonthlySummaryByUser(userID);

        List<TransactionsMonthlySummaryDto> transactionsMonthlySummary = result.stream()
                .map(data -> new TransactionsMonthlySummaryDto(
                        ((Number) data[0]).intValue(),  // month
                        ((Number) data[2]).doubleValue(),  // income
                        ((Number) data[3]).doubleValue()   // expense
                )).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionsMonthlySummary
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getMonthlySummaryByCategory(Long userID) {

        List<Object[]> result = transactionRepository.findTotalAmountByAllCategories(userID);

        List<TransactionSummaryByCategory> transactionsMonthlySummary = result.stream()
                .map(data -> new TransactionSummaryByCategory(
                        (int) data[0],
                        ((ETransactionType) data[1]).name(),
                        (String) data[2],
                        (double) data[3]
                )).toList();

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS,
                        HttpStatus.OK,
                        transactionsMonthlySummary
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getMonthlyIncomeExpenseChartData(Long userId, int year) {
        List<Object[]> monthlyData = transactionRepository.sumIncomeAndExpenseByMonth(userId, year);

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Object[] row : monthlyData) {
            Map<String, Object> monthEntry = new HashMap<>();
            monthEntry.put("month", row[0]);
            monthEntry.put("income", row[1]);
            monthEntry.put("expense", row[2]);
            chartData.add(monthEntry);
        }

        return ResponseEntity.ok(new ApiResponseDto<>(ApiResponseStatus.SUCCESS, HttpStatus.OK, chartData));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getCategoryBreakdownChartData(Long userId, int month, int year) {
        List<Object[]> categoryTotals = transactionRepository.sumByCategory(userId, month, year);

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Object[] row : categoryTotals) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("category", row[0]);
            entry.put("total", row[1]);
            chartData.add(entry);
        }

        return ResponseEntity.ok(new ApiResponseDto<>(ApiResponseStatus.SUCCESS, HttpStatus.OK, chartData));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getDailyIncomeExpenseChartData(Long userId, int month, int year) {
        List<Object[]> dailyData = transactionRepository.sumIncomeAndExpenseByDay(userId, month, year);

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Object[] row : dailyData) {
            Map<String, Object> dayEntry = new HashMap<>();
            dayEntry.put("day", row[0]);
            dayEntry.put("income", row[1]);
            dayEntry.put("expense", row[2]);
            chartData.add(dayEntry);
        }

        return ResponseEntity.ok(new ApiResponseDto<>(ApiResponseStatus.SUCCESS, HttpStatus.OK, chartData));
    }

    @Override
    public ResponseEntity<ApiResponseDto<?>> getYearlyIncomeExpenseChartData(Long id, int year) {
        List<Object[]> yearlyData = transactionRepository.sumIncomeAndExpenseByYear(id, year);
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (Object[] row : yearlyData) {
            Map<String, Object> yearEntry = new HashMap<>();
            yearEntry.put("year", row[0]);
            yearEntry.put("income", row[1]);
            yearEntry.put("expense", row[2]);
            chartData.add(yearEntry);
        }
        return ResponseEntity.ok(new ApiResponseDto<>(ApiResponseStatus.SUCCESS, HttpStatus.OK, chartData));
    }


}
