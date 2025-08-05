package com.trading.simulation.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.trading.simulation.other.Holding;
import com.trading.simulation.other.Transaction;
import com.trading.simulation.other.User;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

@Service
public class Trading {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean canSell(Holding holding, double quantity) {
        return holding.getQuantity() >= quantity;
    }

    public boolean canBuy(User user, double quantity, double price) {
        return user.getBalance() >= (quantity * price);
    }

    public void buy(User user, String cryptoSymbol, double quantity, double price, String cryptoName) {
        double total = quantity * price;
        user.setBalance(user.getBalance() - total);
        Holding holding = findHoldingsByUserAndCrypto(user.getId(), cryptoSymbol);
        if (holding != null) {
            holding.setQuantity(holding.getQuantity() + quantity);
            updateHolding(holding);
        } else {
            holding = new Holding(null, user.getId(), cryptoSymbol, quantity, cryptoName);
            saveHolding(holding);
        }

        Transaction transaction = new Transaction(null, user.getId(), cryptoSymbol, quantity, price, "buy",
                java.time.LocalDateTime.now(), "");
        saveTransaction(transaction);
        updateUserBalance(user.getId(), user.getBalance());
    }

    public boolean sell(User user, String cryptoSymbol, double quantity, double price, String cryptoName) {
        Holding holding = findHoldingsByUserAndCrypto(user.getId(), cryptoSymbol);
        if(!canSell(holding, quantity)) return false;

        double total = quantity * price;
        user.setBalance(user.getBalance() + total);
        holding.setQuantity(holding.getQuantity() - quantity);

        if (holding.getQuantity() == 0) {
            deleteHolding(holding);
        } else {
            updateHolding(holding);
        }

        Transaction transaction = new Transaction(null, user.getId(), cryptoSymbol, quantity, price, "sell",
                java.time.LocalDateTime.now(), cryptoName);
        saveTransaction(transaction);
        updateUserBalance(user.getId(), user.getBalance());
        return true;
    }

    public void reset(Integer userId, double startBalance) {
        updateUserBalance(userId, startBalance);
        clearHoldings(userId);
        clearTransactions(userId);
    }

    private Holding findHoldingsByUserAndCrypto(Integer userId, String cryptoSymbol) {
        List<Holding> holdings = jdbcTemplate.query(
                "SELECT * FROM holdings WHERE user_id = ? AND crypto_symbol = ?",
                new BeanPropertyRowMapper<>(Holding.class), userId, cryptoSymbol);
        return holdings.isEmpty() ? null : holdings.get(0);
    }

    private void updateUserBalance(Integer userId, Double balance) {
        jdbcTemplate.update("UPDATE users SET balance = ? WHERE id = ?",
                balance, userId);
    }

    private void saveHolding(Holding holding) {
        jdbcTemplate.update("INSERT INTO holdings (user_id, crypto_symbol, quantity) VALUES (?, ?, ?)",
                holding.getUserId(), holding.getCryptoSymbol(), holding.getQuantity());
    }

    private void updateHolding(Holding holding) {
        jdbcTemplate.update("UPDATE holdings SET quantity = ? WHERE user_id = ? AND crypto_symbol = ?",
                holding.getQuantity(), holding.getUserId(), holding.getCryptoSymbol());
    }

    private void deleteHolding(Holding holding) {
        jdbcTemplate.update("DELETE FROM holdings WHERE id = ?", holding.getId());
    }

    private void saveTransaction(Transaction transaction) {
        jdbcTemplate.update(
                "INSERT INTO transactions (user_id, crypto_symbol, quantity, price, type, time_purchase) VALUES (?, ?, ?, ?, ?, ?)",
                transaction.getUserId(), transaction.getCryptoSymbol(), transaction.getQuantity(),
                transaction.getPrice(), transaction.getType(), transaction.getTimePurchase());
    }

    private void clearHoldings(Integer userId) {
        jdbcTemplate.update("DELETE FROM holdings WHERE user_id = ?", userId);
    }

    private void clearTransactions(Integer userId) {
        jdbcTemplate.update("DELETE FROM transactions WHERE user_id = ?", userId);
    }
}
