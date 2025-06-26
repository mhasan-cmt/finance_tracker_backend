package com.fullStack.expenseTracker.dto.reponses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryByCategory {
    int categoryId;
    String categoryType;
    String categoryName;
    double total;
}
