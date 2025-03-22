package com.fullStack.expenseTracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    private String categoryName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_type_id")
    private TransactionType transactionType;

    private boolean enabled;


    public Category(String categoryName, TransactionType transactionType, boolean enabled) {
        this.categoryName = categoryName;
        this.transactionType = transactionType;
        this.enabled = enabled;
    }
}
