package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.User;
import com.fullStack.expenseTracker.models.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
    Optional<UserPoints> findByUser(User user);
    
    Optional<UserPoints> findByUserId(Long userId);
    
    @Query("SELECT up.points FROM UserPoints up WHERE up.user.id = :userId")
    Integer findPointsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserPoints up WHERE up.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}