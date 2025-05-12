package com.fullStack.expenseTracker.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "savings_goal")
public class SavingsGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "goal_id")
    private Long goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "target_amount", nullable = false)
    private double targetAmount;

    @Column(name = "current_amount", nullable = false)
    private double currentAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(nullable = false)
    private boolean completed;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    public SavingsGoal(User user, String name, String description, double targetAmount, 
                      LocalDate startDate, LocalDate targetDate, Category category) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = 0;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.completed = false;
        this.category = category;
    }

    public double getProgressPercentage() {
        if (targetAmount <= 0) {
            return 0;
        }
        return (currentAmount / targetAmount) * 100;
    }

    public boolean isCompleted() {
        return currentAmount >= targetAmount;
    }

    public void addContribution(double amount) {
        this.currentAmount += amount;
        this.completed = isCompleted();
    }
}