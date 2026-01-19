package com.expensetracker.expense_tracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive and greater than 0")
    @Schema(description = "Amount spent", example = "150.00")
    private BigDecimal amount;
    @Schema(description = "Short Description of the expense", example = "Lunch at Wimpy")
    private String description;

    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of the category", example = "2")
    private Long categoryId;

    private LocalDate expenseDate;

}
