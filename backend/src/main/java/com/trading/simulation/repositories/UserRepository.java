package com.trading.simulation.repositories;

import com.trading.simulation.other.Holding;
import com.trading.simulation.other.Transaction;
import com.trading.simulation.other.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
    }

    public User findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email, password);
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public void insertUser(User user) {
        String sql = "INSERT INTO users (name, email, password, balance) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getPassword(), user.getBalance());
    }

    public void updateBalance(Integer id, Double balance) {
        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        jdbcTemplate.update(sql, balance, id);
    }

    public void clearHoldings(Integer userId) {
        String sql = "DELETE FROM holdings WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public void clearTransactions(Integer userId) {
        String sql = "DELETE FROM transactions WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    public Double getTotalBuy(Integer id) {
        String sql = "SELECT COALESCE(SUM(quantity * price), 0) FROM transactions WHERE user_id = ? AND type = 'BUY'";
        return jdbcTemplate.queryForObject(sql, Double.class, id);
    }

    public Double getTotalSell(Integer id) {
        String sql = "SELECT COALESCE(SUM(quantity * price), 0) FROM transactions WHERE user_id = ? AND type = 'SELL'";
        return jdbcTemplate.queryForObject(sql, Double.class, id);
    }

    public List<Holding> getHoldings(Integer id) {
        String sql = """
                    SELECT h.id AS id, h.user_id AS userId, h.crypto_symbol AS cryptoSymbol,
                    c.name AS cryptoName, h.quantity AS quantity FROM holdings h
                    JOIN cryptos c ON h.crypto_symbol = c.symbol
                    WHERE h.user_id = ?
                """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Holding.class), id);
    }

    public List<Transaction> getHistory(Integer id) {
        String sql = """
                    SELECT t.id AS id, t.user_id AS userId, t.crypto_symbol AS cryptoSymbol,
                    c.name AS cryptoName, t.type AS type, t.quantity AS quantity,
                    t.price AS price, t.time_purchase AS timePurchase FROM transactions t
                    JOIN cryptos c ON t.crypto_symbol = c.symbol
                    WHERE t.user_id = ? ORDER BY time_purchase DESC
                """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Transaction.class), id);
    }
}
