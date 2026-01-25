package com.expensetracker.expense_tracker.controller;

import com.expensetracker.expense_tracker.dto.ExpenseCategorySummary;
import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * Create expense.
     * Example POST /api/expenses/{userId}&categoryId=2
     * Body: { "categoryId":1,"amount": 120.00, "description":"food"}
     */
    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {

        ExpenseResponse newExpense = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newExpense);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserExpenses(@RequestParam(required = false) String period,
                                                               @RequestParam(required = false) LocalDate startDate,
                                                               @RequestParam(required = false) LocalDate endDate,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Page<ExpenseResponse> expensePage = expenseService.getAllUserExpenses(period, startDate, endDate, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("count", expensePage.getTotalElements());
        response.put("expenses", expensePage.getContent());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> delete(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long expenseId,
                                                         @RequestBody ExpenseRequest request) {
        ExpenseResponse updated = expenseService.updateUserExpense(expenseId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);

    }

    @Operation(summary = "Get expense summary grouped by category",
            description = "Returns total spending per category for the current user")

    @GetMapping("/summary")
    public ResponseEntity<List<ExpenseCategorySummary>> getCategoryStats(@RequestParam(required = false) String period,
                                                                         @RequestParam(required = false) LocalDate startDate,
                                                                         @RequestParam(required = false) LocalDate endDate) {
        return ResponseEntity.ok(expenseService.getExpenseSummary(period, startDate, endDate));
    }
    @Operation(summary = "Export expenses to CSV",
            description = "Downloads a CSV file containing all expenses for the current authenticated user")
    @GetMapping("/export")
    public void exportExpense(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"expenses.csv\"");
        expenseService.exportExpensesToCsv(response.getWriter());
    }

}
