package com.expensetracker.expense_tracker.service.impl;

import com.expensetracker.expense_tracker.entity.User;
import com.expensetracker.expense_tracker.repository.UserRepository;
import com.expensetracker.expense_tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService {
    private final UserRepository repository;
    @Override
    public User getCurrentUser() {
         var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String email;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            // Fallback for custom user objects, assuming they have a toString() or similar
            email = principal.toString();
        }

        // 4. Use the email to load the REAL Entity from YOUR database
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
