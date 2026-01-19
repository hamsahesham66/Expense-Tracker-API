package com.expensetracker.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategorySummary {
    private String categoryName;
    private BigDecimal totalAmount;

}
