package com.trading.simulation.controllers;

import com.trading.simulation.other.User;
import com.trading.simulation.services.TradingService;
import com.trading.simulation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trade")
@CrossOrigin("http://localhost:3000")
public class TradeController {

    @Autowired
    private TradingService tradingService;

    @Autowired
    private UserService userService;

    @PostMapping("/buy/{userId}")
    public ResponseEntity<String> buyCrypto(
            @PathVariable int userId,
            @RequestParam String cryptoSymbol,
            @RequestParam double quantity,
            @RequestParam double price,
            @RequestParam String cryptoName) {

        try {
            User user = userService.getUserById(userId);

            if (!tradingService.canBuy(user, quantity, price)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Not enough balance!");
            }

            tradingService.buy(user, cryptoSymbol, quantity, price, cryptoName);
            return ResponseEntity.ok("Bought successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/sell/{userId}")
    public ResponseEntity<String> sellCrypto(
            @PathVariable int userId,
            @RequestParam String cryptoSymbol,
            @RequestParam double quantity,
            @RequestParam double price,
            @RequestParam String cryptoName) {

        try {
            User user = userService.getUserById(userId);

            if (tradingService.sell(user, cryptoSymbol, quantity, price, cryptoName)) {
                return ResponseEntity.ok("Sold successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Not enough amount!");
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }
}
