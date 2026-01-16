package com.expensetracker.expense_tracker.service.impl;

import com.expensetracker.expense_tracker.entity.Category;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.entity.User;
import com.expensetracker.expense_tracker.exception.BadRequestException;
import com.expensetracker.expense_tracker.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker.repository.CategoryRepository;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import com.expensetracker.expense_tracker.repository.UserRepository;
import com.expensetracker.expense_tracker.service.ExpenseService;
import com.expensetracker.expense_tracker.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        if (expense == null) {
            throw new BadRequestException("Expense data is required");
        }
        if (expense.getAmount() == null || expense.getAmount().doubleValue() <= 0.0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        User user = userService.getCurrentUser();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: "+categoryId));
        expense.setUser(user);
        expense.setCategory(category);
        return expenseRepository.save(expense);
    }

    @Override
    public Page<Expense> getAllUserExpenses(String period, LocalDate start, LocalDate end, int page, int size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("expenseDate").descending());
        if (period == null || period.isEmpty()) {
            return expenseRepository.findByUser(user,pageable);
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
                return expenseRepository.findByUser(user,pageable);
        }
        return expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate,pageable);
    }

    @Override
    public void deleteExpense(Long expenseId) {
        User user = userService.getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found or access denied"));
        expenseRepository.delete(expense);
    }

    @Override
    public Expense updateUserExpense(Long expenseId, Expense expenseReq) {
        if (expenseReq == null) {
            throw new BadRequestException("Update data is required");
        }

        User user = userService.getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found or access denied"));
        if (expenseReq.getAmount() != null) {
            if (expenseReq.getAmount().doubleValue() <= 0.0) {
                throw new BadRequestException("Amount must be greater than zero");
            }
            expense.setAmount(expenseReq.getAmount());
        }
        if (expenseReq.getDescription() != null) {
            expense.setDescription(expenseReq.getDescription().trim());
        }
        if (expenseReq.getCategoryId() != null) {
            Category category = categoryRepository.findById(expenseReq.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found "+expenseReq.getCategoryId()));
            expense.setCategory(category);
        }

        return expenseRepository.save(expense);
    }
}
