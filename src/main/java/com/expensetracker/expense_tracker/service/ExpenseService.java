package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.entity.Expense;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
     Expense createExpense(Expense expense, Long categoryId);
    Page<Expense> getAllUserExpenses(String period, LocalDate start, LocalDate End, int page, int size);
     void deleteExpense(Long expenseId);
    Expense updateUserExpense(Long expenseId,Expense expenseReq);

}
