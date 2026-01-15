package com.expensetracker.expense_tracker.service.impl;

import com.expensetracker.expense_tracker.entity.Category;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.entity.User;
import com.expensetracker.expense_tracker.repository.CategoryRepository;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import com.expensetracker.expense_tracker.repository.UserRepository;
import com.expensetracker.expense_tracker.service.ExpenseService;
import com.expensetracker.expense_tracker.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Override
    public Expense createExpense(Expense expense, Long categoryId) {
        User user = userService.getCurrentUser();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        expense.setUser(user);
        expense.setCategory(category);
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> getAllUserExpenses(String period, LocalDate start,LocalDate end) {
        User user = userService.getCurrentUser();
        if (period == null || period.isEmpty()) {
            return expenseRepository.findByUser(user);
        }
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();
        switch (period.toLowerCase()) {
            case "week":
                startDate = LocalDate.now().minusWeeks(1);
                break;

            case "month":
                startDate = LocalDate.now().minusMonths(1);
                break;

            case "3months":
                startDate = LocalDate.now().minusMonths(3);
                break;

            case "custom":
                if (start == null || end == null) {
                    throw new IllegalArgumentException("Start and End date are required for custom filter");
                }
                startDate = start;
                endDate = end;
                break;

            default:
                return expenseRepository.findByUser(user);
        }
        return expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate);
    }

    @Override
    public void deleteExpense(Long expenseId) {
        User user = userService.getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found or access denied"));
        expenseRepository.delete(expense);
    }

    @Override
    public Expense updateUserExpense(Long expenseId, Expense expenseReq) {
        User user = userService.getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found or access denied"));
        if (expenseReq.getAmount() != null) {
            expense.setAmount(expenseReq.getAmount());
        }
        if (expenseReq.getDescription() != null) {
            expense.setDescription(expenseReq.getDescription());
        }
        if (expenseReq.getCategoryId() != null) {
            Category category = categoryRepository.findById(expenseReq.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            expense.setCategory(category);
        }

        return expenseRepository.save(expense);
    }
}
