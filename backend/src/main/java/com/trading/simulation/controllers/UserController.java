package com.trading.simulation.controllers;

import com.trading.simulation.other.User;
import com.trading.simulation.services.Trading;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("http://localhost:3000")
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Trading trading;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User loginUser) {
        try {
            User user = jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE email = ? AND password = ?",
                    new BeanPropertyRowMapper<>(User.class),
                    loginUser.getEmail(),
                    loginUser.getPassword());
            return ResponseEntity.ok(user);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class, user.getEmail());

        if (cnt != null && cnt > 0) {
            return "Email already exists";
        }

        jdbcTemplate.update(
                "INSERT INTO users (name, email, password, balance) VALUES (?, ?, ?, ?)",
                user.getName(), user.getEmail(), user.getPassword(), 10000.0);

        return "Registration successful";
    }

    @GetMapping("/get/{id}")
    public User getUserById(@PathVariable Integer id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                new BeanPropertyRowMapper<>(User.class), id);
    }

    @PutMapping("/reset/{id}")
    public ResponseEntity<String> resetUser(@PathVariable Integer id) {
        trading.reset(id, 10000.00);
        return ResponseEntity.ok("User reset successfully");
    }

    

}
