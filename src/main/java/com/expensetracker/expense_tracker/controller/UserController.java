package com.expensetracker.expense_tracker.controller;

import com.expensetracker.expense_tracker.entity.User;
import com.expensetracker.expense_tracker.service.UerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {
    private final UerService userService;

    public UserController(UerService userService) {
        this.userService = userService;
    }
    @PostMapping
    public User create(@RequestBody User user){
        return userService.createUser(user);
    }
    @GetMapping
    public List<User> getAll(){
        return userService.getAllUsers();
    }
}
