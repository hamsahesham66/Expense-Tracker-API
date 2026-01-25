package com.expensetracker.expense_tracker.service.impl;

import com.expensetracker.expense_tracker.dto.ExpenseCategorySummary;
import com.expensetracker.expense_tracker.dto.ExpenseMapper;
import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
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

import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final ExpenseMapper expenseMapper;

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request) {
        User user = userService.getCurrentUser();

        if (request == null) {
            throw new BadRequestException("Expense data is required");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Expense expense = expenseMapper.toEntity(request);
        expense.setUser(user);
        expense.setCategory(category);
        Expense savedExpense = expenseRepository.save(expense);

        return expenseMapper.toResponse(savedExpense);
    }

    @Override
    public Page<ExpenseResponse> getAllUserExpenses(String period, LocalDate start, LocalDate end, int page, int size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("expenseDate").descending());
        if (period == null || period.isEmpty()) {
            return expenseRepository.findByUser(user, pageable).map((expenseMapper::toResponse));
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
                return expenseRepository.findByUser(user, pageable)
                        .map(expenseMapper::toResponse);
        }
        return expenseRepository.findByUserAndExpenseDateBetween(user, startDate, endDate, pageable)
                .map(expenseMapper::toResponse);
    }

    @Override
    public void deleteExpense(Long expenseId) {
        User user = userService.getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found or access denied"));
        expenseRepository.delete(expense);
    }

    @Override
    public ExpenseResponse updateUserExpense(Long expenseId, ExpenseRequest request) {
        User user = userService.getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(expenseId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found or access denied"));

        if (request.getAmount() != null) {
            if (request.getAmount().doubleValue() <= 0) {
                throw new BadRequestException("Amount must be greater than zero");
            }
            expense.setAmount(request.getAmount());
        }
        if (request.getDescription() != null) {
            expense.setDescription(request.getDescription().trim());
        }
        if (request.getExpenseDate() != null) {
            expense.setExpenseDate(request.getExpenseDate());
        }

        if (request.getCategoryId() != null) {
            if (!expense.getCategory().getId().equals(request.getCategoryId())) {
                Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + request.getCategoryId()));
                expense.setCategory(category);
            }
        }

        Expense savedExpense = expenseRepository.save(expense);
        return expenseMapper.toResponse(savedExpense);
    }

    @Override
    public List<ExpenseCategorySummary> getExpenseSummary(String period, LocalDate start, LocalDate end) {
        User user = userService.getCurrentUser();
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();
        if (period == null || period.isEmpty()) {
            startDate = LocalDate.now().minusMonths(1); // default
        } else {
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
                        throw new IllegalArgumentException(
                                "Start and end dates are required for custom period"
                        );
                    }
                    startDate = start;
                    endDate = end;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid period value");
            }
        }
        return expenseRepository.getCategoryStats(user, startDate, endDate);
    }

    @Override
    public void exportExpensesToCsv(Writer writer) {
        User user = userService.getCurrentUser();
        List<Expense> expenses = expenseRepository.findByUserId(user.getId());
        try (PrintWriter printWriter = new PrintWriter(writer)) {
            // 2. Write the CSV Header
            printWriter.println("ID,Amount,Description,Category,Date");

            // 3. Write each expense as a row
            for (Expense expense : expenses) {
                printWriter.printf("%d,%s,%s,%s,%s%n",
                        expense.getId(),
                        expense.getAmount(),
                        escapeCsv(expense.getDescription()),
                        expense.getCategory().getCategoryName(),
                        expense.getExpenseDate()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while writing CSV", e);
        }

    }

    private String escapeCsv(String data) {
        if (data == null) return "";
        if (data.contains(",") || data.contains("\n")) {
            return "\"" + data.replace("\"", "\"\"") + "\"";
        }
        return data;
    }
}