package com.expensetracker.expense_tracker.controller;

import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/expenses")
public class ExpenseController {

   private  final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Create expense.
     * Example POST /api/expenses/{userId}&categoryId=2
     * Body: { "categoryId":1,"amount": 120.00, "description":"food"}
     */
    @PostMapping
    public ResponseEntity<Expense> create(@PathVariable Long userId ,
                                          @RequestBody Expense expense
                                          ){
        Long categoryId=expense.getCategoryId();
        Expense saved=expenseService.createExpense(expense,categoryId,userId);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getUserExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getUserExpenses(userId));
    }
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> delete(@PathVariable Long userId,@PathVariable Long expenseId ){
        expenseService.deleteExpense(userId, expenseId);
        return ResponseEntity.ok(Map.of("message","deleted"));
    }

}
