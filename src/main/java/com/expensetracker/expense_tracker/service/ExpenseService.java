package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.entity.Expense;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
     Expense createExpense(Expense expense, Long categoryId);
     List<Expense> getAllUserExpenses(String period, LocalDate start,LocalDate End);
     void deleteExpense(Long expenseId);
    Expense updateUserExpense(Long expenseId,Expense expenseReq);

}
