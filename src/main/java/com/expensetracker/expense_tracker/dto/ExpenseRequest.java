package com.expensetracker.expense_tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive and greater than 0")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private LocalDate expenseDate;

}
