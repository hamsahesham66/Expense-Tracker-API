package com.expensetracker.expense_tracker.controller;

import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.entity.Category;
import com.expensetracker.expense_tracker.entity.User;
import com.expensetracker.expense_tracker.repository.CategoryRepository;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import com.expensetracker.expense_tracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExpenseControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 1. Wipe the H2 database clean
        expenseRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        // 2. Insert a Category
        Category food = new Category();
        food.setCategoryName("Food & Dining");
        categoryRepository.save(food);

        User testUser = new User();
        testUser.setFullName("Test User");
        testUser.setEmail("testuser");
        testUser.setPassword("password");
        userRepository.save(testUser);

    }
    @Test
    @WithMockUser(username = "testuser") // Simulate a logged-in user
    void createExpense_ShouldReturn201_WhenValid() throws Exception {

        Long categoryId = categoryRepository.findAll().get(0).getId();

        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("150.00"));
        request.setDescription("Integration Test Lunch");
        request.setCategoryId(categoryId);

        // --- WHEN & THEN ---
        mockMvc.perform(post("/api/users/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Convert object to JSON

                // Expect "201 Created"
                .andExpect(status().isCreated())

                // Expect the JSON response to contain our data
                .andExpect(jsonPath("$.amount", is(150.00)))
                .andExpect(jsonPath("$.description", is("Integration Test Lunch")));
    }
}
