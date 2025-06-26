package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.MonthlySummary;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.security.UserDetailsImpl;
import com.fullStack.expenseTracker.util.LinearRegressionUtils;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prediction")
@RequiredArgsConstructor
public class PredictionController {

    private final TransactionRepository transactionRepo;

    @GetMapping("/next-month")
    public ResponseEntity<?> predictNextMonth(
            @AuthenticationPrincipal UserDetailsImpl principal
    ) {
        List<Object[]> rawSummaries = transactionRepo.getMonthlySummariesNative(principal.getId(), 1);
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

                    return new MonthlySummary(
                            date,
                            ((Double) row[1])
                    );
                })
                .toList();

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

        return ResponseEntity.ok(response);
    }
}
