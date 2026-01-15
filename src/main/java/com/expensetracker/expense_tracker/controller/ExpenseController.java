package com.expensetracker.expense_tracker.controller;

import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/expenses")
@RequiredArgsConstructor
public class ExpenseController {

   private  final ExpenseService expenseService;

    /**
     * Create expense.
     * Example POST /api/expenses/{userId}&categoryId=2
     * Body: { "categoryId":1,"amount": 120.00, "description":"food"}
     */
    @PostMapping
    public ResponseEntity<Expense> create(@RequestBody Expense expense){
        
        Expense saved = expenseService.createExpense(expense, expense.getCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getUserExpenses(@RequestParam(required = false) String period,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate) {
        List<Expense> expenses = expenseService.getAllUserExpenses(period, startDate, endDate);
        return ResponseEntity.ok(expenses);
    }
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> delete(@PathVariable Long expenseId ){
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long expenseId,
                                                    @RequestBody Expense expense){
       Expense saved= expenseService.updateUserExpense(expenseId,expense);
        return ResponseEntity.status(HttpStatus.OK).body(saved);

    }

}
