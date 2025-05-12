package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.models.Category;
import com.fullStack.expenseTracker.models.TransactionType;
import com.fullStack.expenseTracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    boolean existsByCategoryNameAndTransactionType(String categoryName, TransactionType transactionType);

    boolean existsByCategoryNameAndTransactionTypeAndUser(String categoryName, TransactionType transactionType, User user);

    @Query("SELECT c FROM Category c WHERE c.user = :user OR c.user IS NULL")
    List<Category> findByUserOrGlobal(@Param("user") User user);

    List<Category> findByUser(User user);

}
