package com.expensetracker.expense_tracker.dto;

import com.expensetracker.expense_tracker.entity.Expense;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {
    public ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .categoryName(expense.getCategory().getCategoryName())
                .createdAt(expense.getCreatedAt())
                .build();
    }
    public Expense toEntity(ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        return expense;
    }
}
