package com.trading.simulation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cryptos")
@CrossOrigin("http://localhost:3000")
public class CryptoController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public List<Map<String, Object>> getCryptos() {
        return jdbcTemplate.queryForList("SELECT * FROM cryptos");
    }
}

