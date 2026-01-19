package com.expensetracker.expense_tracker.repository;

import com.expensetracker.expense_tracker.dto.ExpenseCategorySummary;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByUserId(Long userId);
    Page<Expense> findByUser(User user, Pageable pageable);
    Optional<Expense> findByIdAndUser(Long id, User user);
    Page<Expense> findByUserAndExpenseDateBetween(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);
    @Query("SELECT new com.expensetracker.expense_tracker.dto.ExpenseCategorySummary(e.category.categoryName, SUM(e.amount)) " +
            "FROM Expense e " +
            "WHERE e.user = :user AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "GROUP BY e.category.categoryName")
    List<ExpenseCategorySummary> getCategoryStats(User user, LocalDate startDate, LocalDate endDate);
}
