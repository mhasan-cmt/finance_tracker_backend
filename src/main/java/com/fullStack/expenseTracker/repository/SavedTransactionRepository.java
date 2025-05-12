package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.SavedTransaction;
import com.fullStack.expenseTracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedTransactionRepository extends JpaRepository<SavedTransaction, Long> {
    List<SavedTransaction> findByUserOrderByUpcomingDateAsc(User user);
}
