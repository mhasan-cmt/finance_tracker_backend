package com.fullStack.expenseTracker.controllers;

import com.fullStack.expenseTracker.dto.reponses.MonthlySummary;
import com.fullStack.expenseTracker.repository.TransactionRepository;
import com.fullStack.expenseTracker.util.LinearRegressionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prediction")
@RequiredArgsConstructor
public class PredictionController {

    private final TransactionRepository transactionRepo;

    @GetMapping("/next-month")
    public ResponseEntity<?> predictNextMonth(
            @RequestParam Long userId
    ) {
        List<MonthlySummary> summaries = transactionRepo.getMonthlySummaries(userId, 1);

        double[] totals = summaries.stream()
                .mapToDouble(MonthlySummary::getTotal)
                .toArray();

        double prediction = LinearRegressionUtils.predictNext(totals);

        Map<String, Object> response = new HashMap<>();
        response.put("predictedAmount", prediction);
        response.put("type", 1);

        return ResponseEntity.ok(response);
    }
}
