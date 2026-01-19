package com.expensetracker.expense_tracker.service.impl;

import com.expensetracker.expense_tracker.dto.ExpenseMapper;
import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
import com.expensetracker.expense_tracker.entity.Category;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.entity.User;
import com.expensetracker.expense_tracker.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker.repository.CategoryRepository;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import com.expensetracker.expense_tracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserService userService;
    @Mock
    private ExpenseMapper expenseMapper;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Test
    void createExpense_whenValidRequest_savesAndReturnsResponse() {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDescription("Lunch");
        request.setExpenseDate(LocalDate.now());
        request.setCategoryId(1L);

        User mockUser = new User();
        mockUser.setId(10L);

        Category mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setCategoryName("Food");

        Expense mappedExpense = new Expense();
        mappedExpense.setAmount(request.getAmount());
        mappedExpense.setDescription(request.getDescription());
        mappedExpense.setExpenseDate(request.getExpenseDate());

        Expense savedExpense = new Expense();
        savedExpense.setId(100L);
        savedExpense.setAmount(request.getAmount());
        savedExpense.setDescription(request.getDescription());
        savedExpense.setExpenseDate(request.getExpenseDate());
        savedExpense.setUser(mockUser);
        savedExpense.setCategory(mockCategory);

        ExpenseResponse expectedResponse = new ExpenseResponse();
        expectedResponse.setId(55L);
        expectedResponse.setAmount(request.getAmount());
        //script
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(expenseMapper.toEntity(request)).thenReturn(mappedExpense);
        when(expenseRepository.save(mappedExpense)).thenReturn(savedExpense);
        when(expenseMapper.toResponse(savedExpense)).thenReturn(expectedResponse);

        ExpenseResponse actualResponse = expenseService.createExpense(request);
        assertNotNull(actualResponse);
        assertEquals(55L, actualResponse.getId());
        assertEquals(new BigDecimal("100.00"), actualResponse.getAmount());
        verify(expenseRepository).save(mappedExpense);


    }
    @Test
    void createExpense_ShouldThrowException_WhenCategoryDoesNotExist() {

        ExpenseRequest request = new ExpenseRequest();
        request.setCategoryId(999L); // ID that doesn't exist
        request.setAmount(new BigDecimal("50.00"));

        // Script: When repo looks for ID 999, return Empty (Not Found)
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // --- WHEN & THEN (The Crash Check) ---
        // We expect the service to throw an exception (ResourceNotFoundException)
        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.createExpense(request);
        });

        // Make sure we did NOT try to save anything to the DB
        verify(expenseRepository, never()).save(any());
    }
}