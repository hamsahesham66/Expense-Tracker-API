package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.entity.Expense;

import java.util.List;

public interface ExpenseService {
     Expense createExpense(Expense expense, Long categoryId,Long userId);
     List<Expense> getUserExpenses(Long userId);
     void deleteExpense(Long userId,Long expenseId);
}
