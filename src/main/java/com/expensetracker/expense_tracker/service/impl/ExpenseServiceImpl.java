package com.expensetracker.expense_tracker.service.impl;

import com.expensetracker.expense_tracker.entity.Category;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.entity.User;
import com.expensetracker.expense_tracker.repository.CategoryRepository;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import com.expensetracker.expense_tracker.repository.UserRepository;
import com.expensetracker.expense_tracker.service.ExpenseService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

        private  final ExpenseRepository expenseRepository;
        private final UserRepository userRepository;
        private  final CategoryRepository categoryRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Expense createExpense(Expense expense,Long categoryId, Long userId){
            User user=userRepository.findById(userId).orElseThrow(()->
                    new RuntimeException("User not found with id: " + userId));
            Category category=categoryRepository.findById(categoryId).orElseThrow(()->
                    new RuntimeException("Category not found"));
                expense.setUser(user);
                expense.setCategory(category);
            return expenseRepository.save(expense);
        }

    @Override
    public List<Expense> getUserExpenses(Long userId) {
            return expenseRepository.findByUserId(userId);
    }

    @Override
    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expenseRepository.delete(expense);

    }

}
