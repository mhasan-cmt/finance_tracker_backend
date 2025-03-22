package com.fullStack.expenseTracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "budget")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "budget_id")
    long budgetId;
    @Column(name = "user_id")
    long userId;
    double amount;
    int month;
    long year;

    public Budget(long userId, double amount, int month, long year) {
        this.userId = userId;
        this.amount = amount;
        this.month = month;
        this.year = year;
    }
}
