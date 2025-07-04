package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.ApiResponseDto;
import com.fullStack.expenseTracker.dto.reponses.MonthlySummary;
import com.fullStack.expenseTracker.enums.ApiResponseStatus;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.security.UserDetailsImpl;
import com.fullStack.expenseTracker.util.LinearRegressionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mywallet/api/prediction")
@RequiredArgsConstructor
public class PredictionController {

    private final TransactionRepository transactionRepo;

    @GetMapping("/next-month")
    public ResponseEntity<?> predictNextMonth(@AuthenticationPrincipal UserDetailsImpl principal, @RequestParam("type") int type) {

        if (type != 1 && type != 2) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Invalid type. Must be 1 for income or 2 for expense.");
            return ResponseEntity.badRequest().body(error);
        }

        List<Object[]> rawSummaries = transactionRepo.getMonthlySummariesNative(principal.getId(), type);
        List<MonthlySummary> summaries = rawSummaries.stream()
                .map(row -> {
                    LocalDate date;
                    if (row[0] instanceof java.sql.Date sqlDate) {
                        date = sqlDate.toLocalDate();
                    } else if (row[0] instanceof java.time.Instant instant) {
                        date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    } else if (row[0] instanceof java.time.LocalDate localDate) {
                        date = localDate;
                    } else {
                        throw new RuntimeException("Unexpected date type: " + row[0].getClass());
                    }

                    return new MonthlySummary(date, ((Double) row[1]));
                })
                .toList();
        if (summaries.size() < 3) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Not enough data to make a prediction. At least 3 months of data is required.");
            error.put("monthsAvailable", summaries.size());
            return ResponseEntity.badRequest().body(error);
        }

        double[] totals = summaries.stream()
                .mapToDouble(MonthlySummary::getTotal)
                .toArray();

        double prediction = LinearRegressionUtils.predictNext(totals);
        double roundedPrediction = new BigDecimal(prediction)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        Map<String, Object> response = new HashMap<>();
        response.put("predictedAmount", roundedPrediction);
        response.put("type", 1);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponseDto<>(ApiResponseStatus.SUCCESS, HttpStatus.OK, response)
        );
    }
}

