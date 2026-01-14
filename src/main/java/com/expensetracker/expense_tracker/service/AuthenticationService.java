package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.dto.AuthenticationRequest;
import com.expensetracker.expense_tracker.dto.AuthenticationResponse;
import com.expensetracker.expense_tracker.dto.RegisterRequest;
import jakarta.validation.Valid;

public interface AuthenticationService {
    AuthenticationResponse register( RegisterRequest request);

    AuthenticationResponse authenticate( AuthenticationRequest request);
}
