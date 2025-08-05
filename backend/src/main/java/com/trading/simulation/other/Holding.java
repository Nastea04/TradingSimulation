package com.trading.simulation.other;

public class Holding {
    private Integer id;
    private Integer userId;
    private String cryptoSymbol;
    private double quantity;
    private String cryptoName;

    public Holding() {
    }

    public Holding(Integer id, Integer userId, String cryptoSymbol, double quantity, String cryptoName) {
        this.id = id;
        this.userId = userId;
        this.cryptoSymbol = cryptoSymbol;
        this.quantity = quantity;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }
}
