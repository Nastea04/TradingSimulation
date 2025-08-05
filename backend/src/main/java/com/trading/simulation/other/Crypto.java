package com.trading.simulation.other;

public class Crypto {
    private String symbol;
    private String name;
    private String url;

    public Crypto(){}
    public Crypto(String symbol, String name, String url)
    {
        this.symbol=symbol;
        this.name=name;
        this.url=url;
    }

    public String getSymbol(){return symbol;}
    public void setSymbol(String symbol){this.symbol=symbol;}

    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public String getUrl(){return url;}
    public void setUrl(String url){this.url=url;}
}
