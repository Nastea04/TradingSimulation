package com.trading.simulation.services;

import com.trading.simulation.other.Holding;
import com.trading.simulation.other.Transaction;
import com.trading.simulation.other.User;
import com.trading.simulation.repositories.HoldingRepository;
import com.trading.simulation.repositories.TransactionRepository;
import com.trading.simulation.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TradingService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HoldingRepository holdingRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    public boolean canSell(Holding holding, Double quantity) {
        return holding != null && holding.getQuantity() >= quantity;
    }

    public boolean canBuy(User user, Double quantity, Double price) {
        return user.getBalance() >= (quantity * price);
    }

    public void buy(User user, String cryptoSymbol, Double quantity, Double price, String cryptoName) {
        Double total = quantity * price;
        user.setBalance(user.getBalance() - total);

        Holding holding = holdingRepo.findByUserAndCrypto(user.getId(), cryptoSymbol);
        if (holding != null) {
            holding.setQuantity(holding.getQuantity() + quantity);
            holdingRepo.update(holding);
        } else {
            holding = new Holding(null, user.getId(), cryptoSymbol, quantity, cryptoName);
            holdingRepo.insert(holding);
        }

        Transaction transaction = new Transaction(null, user.getId(), cryptoSymbol, quantity, price, "buy",
                LocalDateTime.now(), cryptoName);
        transactionRepo.insert(transaction);

        userRepo.updateBalance(user.getId(), user.getBalance());
    }

    public boolean sell(User user, String cryptoSymbol, double quantity, double price, String cryptoName) {
        Holding holding = holdingRepo.findByUserAndCrypto(user.getId(), cryptoSymbol);
        if (!canSell(holding, quantity)) return false;

        double total = quantity * price;
        user.setBalance(user.getBalance() + total);
        holding.setQuantity(holding.getQuantity() - quantity);

        if (holding.getQuantity() == 0) {
            holdingRepo.delete(holding);
        } else {
            holdingRepo.update(holding);
        }

        Transaction transaction = new Transaction(null, user.getId(), cryptoSymbol, quantity, price, "sell",
                LocalDateTime.now(), cryptoName);
        transactionRepo.insert(transaction);

        userRepo.updateBalance(user.getId(), user.getBalance());
        return true;
    }
}
