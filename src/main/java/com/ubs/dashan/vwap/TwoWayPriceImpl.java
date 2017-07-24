package com.ubs.dashan.vwap;

public class TwoWayPriceImpl implements TwoWayPrice {
    private Instrument instrument;
    private State state;
    private Double bidPrice;
    private Double bidAmount;
    private Double offerPrice;
    private Double offerAmount;

    public TwoWayPriceImpl(Instrument instrument, State state, Double bidPrice, Double bidAmount, Double offerPrice, Double offerAmount) {
        this.instrument = instrument;
        this.state = state;
        this.bidPrice = bidPrice;
        this.bidAmount = bidAmount;
        this.offerPrice = offerPrice;
        this.offerAmount = offerAmount;
    }

    @Override
    public Instrument getInstrument() {
        return instrument;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public double getBidPrice() {
        return bidPrice;
    }

    @Override
    public double getOfferAmount() {
        return offerAmount;
    }

    @Override
    public double getOfferPrice() {
        return offerPrice;
    }

    @Override
    public double getBidAmount() {
        return bidAmount;
    }


}
