package com.fullStack.expenseTracker.repository;

import com.fullStack.expenseTracker.enums.ETransactionType;
import com.fullStack.expenseTracker.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Boolean existsByUser_Id(Long userId);

    @Query("SELECT t FROM Transaction t " +
            "JOIN t.category c " +
            "JOIN t.user u " +
            "JOIN c.transactionType tt " +
            "WHERE u.email = :email " +
            "AND (:transactionType IS NULL OR tt.transactionTypeName = :transactionType) " +
            "AND (LOWER(t.description) LIKE LOWER(CONCAT('%', :searchKey, '%')) " +
            "     OR LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :searchKey, '%')))")
    Page<Transaction> findByUser(@Param("email") String email,
                                 Pageable pageable,
                                 @Param("searchKey") String searchKey,
                                 @Param("transactionType") ETransactionType transactionType);


    @Query(value = "SELECT t.*, c.category_id AS c_category_id, c.category_name AS c_category_name, " +
            "u.id AS u_id, u.email AS u_email, " +
            "tt.transaction_type_id AS tt_transaction_type_id, tt.transaction_type_name AS tt_transaction_type_name " +
            "FROM transaction t JOIN category c ON t.category_id = c.category_id JOIN users u ON t.user_id = u.id " +
            "JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id " +
            "WHERE t.description LIKE %:searchKey% OR c.category_name LIKE %:searchKey% OR " +
            "tt.transaction_type_name LIKE %:searchKey% OR u.email LIKE %:searchKey%", nativeQuery = true)
    Page<Transaction> findAll(Pageable pageable, @Param("searchKey") String searchKey);


    @Query(value = "SELECT SUM(t.amount) FROM transaction t " +
            "JOIN users u ON t.user_id = u.id " +
            "JOIN category c ON t.category_id = c.category_id " +
            "JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id " +
            "WHERE u.id = :userId AND tt.transaction_type_id = :transactionTypeId " +
            "AND EXTRACT(MONTH FROM t.date) = :month AND EXTRACT(YEAR FROM t.date) = :year", nativeQuery = true)
    Double findTotalByUserAndTransactionType(@Param("userId") long userId,
                                             @Param("transactionTypeId") Integer transactionTypeId,
                                             @Param("month") int month,
                                             @Param("year") int year);


    @Query(value = "SELECT COUNT(*) FROM transaction t JOIN users u ON t.user_id = u.id " +
            "WHERE u.id = :userId AND date_part('month', t.date) = :month AND date_part('year', t.date) = :year",
            nativeQuery = true)
    Integer findTotalNoOfTransactionsByUser(@Param("userId") long userId,
                                            @Param("month") int month,
                                            @Param("year") int year);

    @Query(value = "SELECT SUM(amount) FROM transaction t " +
            "JOIN users u ON t.user_id = u.id " +
            "JOIN category c ON t.category_id = c.category_id " +
            "WHERE u.email = :email AND c.category_id = :categoryId " +
            "AND EXTRACT(MONTH FROM t.date) = :month AND EXTRACT(YEAR FROM t.date) = :year",
            nativeQuery = true)
    Double findTotalByUserAndCategory(@Param("email") String email,
                                      @Param("categoryId") int categoryId,
                                      @Param("month") int month,
                                      @Param("year") int year);

    @Query(value = """
            SELECT
                EXTRACT(MONTH FROM t.date) AS month_number,
                EXTRACT(YEAR FROM t.date) AS year_number,
                COALESCE(SUM(CASE WHEN tt.transaction_type_name = 'INCOME' THEN t.amount ELSE 0 END), 0) AS income,
                COALESCE(SUM(CASE WHEN tt.transaction_type_name = 'EXPENSE' THEN t.amount ELSE 0 END), 0) AS expense
            FROM transaction t
            JOIN users u ON t.user_id = u.id
            JOIN category c ON t.category_id = c.category_id
            JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
            WHERE u.id = :userId
            AND t.date >= DATE_TRUNC('month', CURRENT_DATE) - INTERVAL '5 months'
            GROUP BY EXTRACT(MONTH FROM t.date), EXTRACT(YEAR FROM t.date)
            ORDER BY year_number, month_number
            """, nativeQuery = true)
    List<Object[]> findMonthlySummaryByUser(@Param("userId") Long userId);


    @Query("SELECT c.categoryId, c.transactionType.transactionTypeName, c.categoryName, SUM(t.amount) " +
            "FROM Transaction t JOIN t.category c " +
            "WHERE t.user.id = :userId " +
            "GROUP BY c.categoryId, c.transactionType, c.categoryName")
    List<Object[]> findTotalAmountByAllCategories(@Param("userId") Long userId);

    @Query(value = """
                WITH months AS (
                    SELECT generate_series(1, 12) AS month
                )
                SELECT
                    m.month,
                    COALESCE(SUM(CASE WHEN tt.transaction_type_name = 'TYPE_INCOME' THEN t.amount ELSE 0 END), 0) AS income,
                    COALESCE(SUM(CASE WHEN tt.transaction_type_name = 'TYPE_EXPENSE' THEN t.amount ELSE 0 END), 0) AS expense
                FROM months m
                LEFT JOIN transaction t ON
                    EXTRACT(MONTH FROM t.date) = m.month AND
                    t.user_id = :userId AND
                    EXTRACT(YEAR FROM t.date) = :year
                LEFT JOIN category c ON t.category_id = c.category_id
                LEFT JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
                GROUP BY m.month
                ORDER BY m.month
            """, nativeQuery = true)
    List<Object[]> sumIncomeAndExpenseByMonth(@Param("userId") Long userId, @Param("year") int year);


    @Query(value = """
                SELECT
                    c.category_name AS categoryName,
                    COALESCE(SUM(t.amount), 0) AS total
                FROM transaction t
                JOIN category c ON t.category_id = c.category_id
                JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
                WHERE
                    t.user_id = :userId
                    AND EXTRACT(MONTH FROM t.date) = :month
                    AND EXTRACT(YEAR FROM t.date) = :year
                    AND tt.transaction_type_name = 'TYPE_EXPENSE'
                GROUP BY c.category_name
                ORDER BY total DESC
            """, nativeQuery = true)
    List<Object[]> sumByCategory(@Param("userId") Long userId,
                                 @Param("month") int month,
                                 @Param("year") int year);


    @Query(value = """
                SELECT
                    EXTRACT(DAY FROM t.date)::integer AS day,
                    COALESCE(SUM(CASE WHEN tt.transaction_type_name = 'TYPE_INCOME' THEN t.amount ELSE 0 END), 0) AS income,
                    COALESCE(SUM(CASE WHEN tt.transaction_type_name = 'TYPE_EXPENSE' THEN t.amount ELSE 0 END), 0) AS expense
                FROM transaction t
                JOIN category c ON t.category_id = c.category_id
                JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
                WHERE
                    t.user_id = :userId
                    AND EXTRACT(MONTH FROM t.date) = :month
                    AND EXTRACT(YEAR FROM t.date) = :year
                GROUP BY EXTRACT(DAY FROM t.date)
                ORDER BY day
            """, nativeQuery = true)
    List<Object[]> sumIncomeAndExpenseByDay(@Param("userId") Long userId,
                                            @Param("month") int month,
                                            @Param("year") int year);

    @Query(value = """
                SELECT
                    EXTRACT(YEAR FROM t.date) AS year,
                    COALESCE(SUM(CASE WHEN tt.transaction_type_name = 'TYPE_INCOME' THEN t.amount ELSE 0 END), 0) AS income,
                    COALESCE(SUM(CASE WHEN tt.transaction_type_name = 'TYPE_EXPENSE' THEN t.amount ELSE 0 END), 0) AS expense
                FROM transaction t
                JOIN category c ON t.category_id = c.category_id
                JOIN transaction_type tt ON c.transaction_type_id = tt.transaction_type_id
                WHERE
                    t.user_id = :id
                    AND EXTRACT(YEAR FROM t.date) = :year
                GROUP BY EXTRACT(YEAR FROM t.date)
            """, nativeQuery = true)
    List<Object[]> sumIncomeAndExpenseByYear(@Param("id") Long id, @Param("year") int year);
}