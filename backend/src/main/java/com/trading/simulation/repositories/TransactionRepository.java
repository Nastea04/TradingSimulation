package com.trading.simulation.repositories;

import com.trading.simulation.other.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class TransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, crypto_symbol, quantity, price, type, time_purchase) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            transaction.getUserId(),
            transaction.getCryptoSymbol(),
            transaction.getQuantity(),
            transaction.getPrice(),
            transaction.getType(),
            transaction.getTimePurchase()
        );
    }

}
