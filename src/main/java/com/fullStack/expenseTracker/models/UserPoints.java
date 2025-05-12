package com.fullStack.expenseTracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_points")
public class UserPoints {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int points;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public UserPoints(User user) {
        this.user = user;
        this.points = 0;
        this.lastUpdated = LocalDateTime.now();
    }

    public void addPoints(int points) {
        if (points > 0) {
            this.points += points;
            this.lastUpdated = LocalDateTime.now();
        }
    }

    public boolean usePoints(int points) {
        if (points > 0 && this.points >= points) {
            this.points -= points;
            this.lastUpdated = LocalDateTime.now();
            return true;
        }
        return false;
    }
}