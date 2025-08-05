package com.trading.simulation.other;

import java.time.LocalDateTime;

public class Transaction {
    private Integer id;
    private Integer userId;
    private String cryptoSymbol;
    private double quantity;
    private double price;
    private String type;// buy or sell
    private LocalDateTime timePurchase;
    private String cryptoName;

    public Transaction() {
    }

    public Transaction(Integer id, Integer userId, String cryptoSymbol, double quantity, double price, String type,
            LocalDateTime timePurchase, String cryptoName) {
        this.id = id;
        this.userId = userId;
        this.cryptoSymbol = cryptoSymbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.timePurchase = timePurchase;
        this.cryptoName = cryptoName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCryptoSymbol() {
        return cryptoSymbol;
    }

    public void setCryptoSymbol(String cryptoSymbol) {
        this.cryptoSymbol = cryptoSymbol;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimePurchase() {
        return timePurchase;
    }

    public void setTimePurchase(LocalDateTime timePurchase) {
        this.timePurchase = timePurchase;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }
}
