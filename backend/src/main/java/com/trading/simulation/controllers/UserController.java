package com.trading.simulation.controllers;

import com.trading.simulation.other.Holding;
import com.trading.simulation.other.Transaction;
import com.trading.simulation.other.User;
import com.trading.simulation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User loginUser) {
        User user = userService.login(loginUser.getEmail(), loginUser.getPassword());
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        return userService.register(user);
    }

    @GetMapping("/get/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/reset/{id}")
    public ResponseEntity<String> resetUser(@PathVariable Integer id) {
        userService.resetUser(id, 10000.00);
        return ResponseEntity.ok("User reset successfully");
    }

    @PutMapping("/profitloss/{id}")
    public Double getProfitLoss(@PathVariable Integer id) {
        return userService.getProfitLoss(id);
    }

    @GetMapping("/holdings/{id}")
    public List<Holding> getHoldings(@PathVariable Integer id) {
        return userService.getHoldings(id);
    }

    @GetMapping("/history/{id}")
    public List<Transaction> getHistory(@PathVariable Integer id) {
        return userService.getHistory(id);
    }

}
