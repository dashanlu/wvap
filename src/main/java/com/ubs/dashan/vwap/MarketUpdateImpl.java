package com.ubs.dashan.vwap;

public class MarketUpdateImpl implements MarketUpdate{
    private Market market;
    private TwoWayPrice twoWayPrice;

    public MarketUpdateImpl(Market market, TwoWayPrice twoWayPrice) {
        this.market = market;
        this.twoWayPrice = twoWayPrice;
    }

    public Market getMarket(){
        return market;
    }

    public TwoWayPrice getTwoWayPrice(){
        return twoWayPrice;
    }
}