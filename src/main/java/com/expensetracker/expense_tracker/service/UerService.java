package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.entity.User;
import com.expensetracker.expense_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class UerService {
    private final UserRepository userRepository;

    public UerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User createUser(User user){
        return userRepository.save(user);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

}
