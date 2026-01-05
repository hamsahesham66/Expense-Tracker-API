package com.expensetracker.expense_tracker.controller;

import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/expenses")
public class ExpenseController {

   private  final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Create expense.
     * Example POST /api/expenses?userId=1&categoryId=2
     * Body: { "amount": 120.00, "description":"food"}
     */
    @PostMapping
    public ResponseEntity<Expense> create(@RequestBody Expense expense,@RequestParam Long userId ,@RequestParam  Long categoryId ){
        Expense saved=expenseService.createExpense(expense,categoryId,userId);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getUserExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getUserExpenses(userId));
    }

}
