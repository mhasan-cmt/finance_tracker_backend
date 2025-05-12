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
@Table(name = "reward")
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reward_id")
    private Long rewardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "points_required", nullable = false)
    private int pointsRequired;

    @Column(name = "is_redeemed", nullable = false)
    private boolean isRedeemed;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Column(name = "redeemed_date")
    private LocalDate redeemedDate;

    public Reward(User user, String name, String description, int pointsRequired) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.isRedeemed = false;
        this.createdDate = LocalDate.now();
    }

    public void redeem() {
        if (!isRedeemed) {
            this.isRedeemed = true;
            this.redeemedDate = LocalDate.now();
        }
    }
}