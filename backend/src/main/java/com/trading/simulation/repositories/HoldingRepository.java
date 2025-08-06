package com.trading.simulation.repositories;

import com.trading.simulation.other.Holding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HoldingRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Holding findByUserAndCrypto(Integer userId, String cryptoSymbol) {
        String sql = "SELECT * FROM holdings WHERE user_id = ? AND crypto_symbol = ?";
        List<Holding> holdings = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Holding.class), userId, cryptoSymbol);
        return holdings.isEmpty() ? null : holdings.get(0);
    }

    public void insert(Holding holding) {
        String sql = "INSERT INTO holdings (user_id, crypto_symbol, quantity) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, holding.getUserId(), holding.getCryptoSymbol(), holding.getQuantity());
    }

    public void update(Holding holding) {
        String sql = "UPDATE holdings SET quantity = ? WHERE user_id = ? AND crypto_symbol = ?";
        jdbcTemplate.update(sql, holding.getQuantity(), holding.getUserId(), holding.getCryptoSymbol());
    }

    public void delete(Holding holding) {
        String sql = "DELETE FROM holdings WHERE id = ?";
        jdbcTemplate.update(sql, holding.getId());
    }
    

}
