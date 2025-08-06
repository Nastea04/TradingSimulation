package com.trading.simulation.services;

import com.trading.simulation.other.Holding;
import com.trading.simulation.other.Transaction;
import com.trading.simulation.other.User;
import com.trading.simulation.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public User login(String email, String password) {
        try {
            return userRepo.findByEmailAndPassword(email, password);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String register(User user) {
        if (userRepo.emailExists(user.getEmail())) {
            return "Email already exists";
        }
        user.setBalance(10000.0);
        userRepo.insertUser(user);
        return "Registration successful";
    }

    public User getUserById(Integer id) {
        return userRepo.findById(id);
    }

    public void updateBalance(Integer id, Double balance) {
        userRepo.updateBalance(id, balance);
    }

    public void resetUser(Integer id, Double startBalance) {
        userRepo.updateBalance(id, startBalance);
        userRepo.clearHoldings(id);
        userRepo.clearTransactions(id);
    }

    public Double getProfitLoss(Integer id) {
        return userRepo.getTotalSell(id) - userRepo.getTotalBuy(id);
    }

    public List<Holding> getHoldings(Integer id) {
        return userRepo.getHoldings(id);
    }

    public List<Transaction> getHistory(Integer id) {
        return userRepo.getHistory(id);
    }

}
