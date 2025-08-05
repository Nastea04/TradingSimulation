package com.trading.simulation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trading.simulation.other.User;

@RestController
@RequestMapping("/api/user")
@CrossOrigin("http://localhost:3000")
public class DashboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/dashboard/{id}")
    public Map<String, Object> getDashboard(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();

        User user = jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                new BeanPropertyRowMapper<>(User.class),
                id);
        result.put("user", user);
        List<Map<String, Object>> cryptos = jdbcTemplate.queryForList("SELECT * FROM cryptos");
        result.put("cryptos", cryptos);
        List<Map<String, Object>> holdings = jdbcTemplate.queryForList(
                """
                            SELECT
                                h.id AS id,
                                h.user_id AS userId,
                                h.crypto_symbol AS cryptoSymbol,
                                c.name AS cryptoName,
                                h.quantity AS quantity
                            FROM holdings h
                            JOIN cryptos c ON h.crypto_symbol = c.symbol
                            WHERE h.user_id = ?
                        """,
                id);
        result.put("holdings", holdings);
        List<Map<String, Object>> history = jdbcTemplate.queryForList(
                """
                            SELECT
                                t.id AS id,
                                t.user_id AS userId,
                                t.crypto_symbol AS cryptoSymbol,
                                c.name AS cryptoName,
                                t.type AS type,
                                t.quantity AS quantity,
                                t.price AS price,
                                t.time_purchase AS timePurchase
                            FROM transactions t
                            JOIN cryptos c ON t.crypto_symbol = c.symbol
                            WHERE t.user_id = ?
                            ORDER BY time_purchase DESC;
                        """, id);
        result.put("history", history);

        Double totalBuy = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(quantity * price), 0) FROM transactions WHERE user_id = ? AND type = 'BUY'",
                Double.class,id);

        Double totalSell = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(quantity * price), 0) FROM transactions WHERE user_id = ? AND type = 'SELL'",
                Double.class,id);

        Double profitLoss = totalSell - totalBuy;
        result.put("profitLoss", profitLoss);

        return result;
    }
}
