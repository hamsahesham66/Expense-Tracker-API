package com.expensetracker.expense_tracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDate expenseDate;
    private String categoryName;
    private LocalDateTime createdAt;
}
