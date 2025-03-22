package com.fullStack.expenseTracker.models;

import com.fullStack.expenseTracker.enums.ETransactionFrequency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "saved_transaction")
public class SavedTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "plan_id")
    private long planId;

    @Column(name = "user_id")
    private long userId;


    @Column(name = "transaction_type_id")
    private int transactionTypeId;

    @Column(name = "category_id")
    private int categoryId;

    private double amount;

    private String description;

    @Enumerated(EnumType.STRING)
    private ETransactionFrequency frequency;

    @Column(name = "upcoming_date")
    private LocalDate upcomingDate;
}
