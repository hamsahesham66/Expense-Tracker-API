package com.expensetracker.expense_tracker.controller;

import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>>getUserExpenses(@RequestParam(required = false) String period,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate,
    @                                                    RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Page<Expense> expensePage = expenseService.getAllUserExpenses(period, startDate, endDate, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("count", expensePage.getTotalElements());
        response.put("expenses", expensePage.getContent());
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> delete(@PathVariable Long expenseId ){
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long expenseId,
                                                    @RequestBody Expense expense){
       Expense updated= expenseService.updateUserExpense(expenseId,expense);
        return ResponseEntity.status(HttpStatus.OK).body(updated);

    }

}
