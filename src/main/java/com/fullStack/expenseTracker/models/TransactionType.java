package com.fullStack.expenseTracker.models;

import com.fullStack.expenseTracker.enums.ETransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transaction_type")
public class TransactionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_type_id")
    private Integer transactionTypeId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "transaction_type_name")
    private ETransactionType transactionTypeName;


    public TransactionType(ETransactionType transactionTypeName) {
        this.transactionTypeName = transactionTypeName;
    }
}
