package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.dto.ExpenseCategorySummary;
import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
import com.expensetracker.expense_tracker.entity.Expense;
import org.springframework.data.domain.Page;

import java.io.Writer;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseRequest request);
    Page<ExpenseResponse> getAllUserExpenses(String period, LocalDate start, LocalDate End, int page, int size);
     void deleteExpense(Long expenseId);
    ExpenseResponse updateUserExpense(Long expenseId,ExpenseRequest request);
    List<ExpenseCategorySummary> getExpenseSummary(String period, LocalDate start, LocalDate end);
    void exportExpensesToCsv(Writer writer);
}
