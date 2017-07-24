package com.ubs.dashan.vwap;

import com.sun.istack.internal.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CalculatorImpl implements Calculator {
    private Map<Instrument, Map<Market, TwoWayPrice>> twoWayPriceManager;

    public CalculatorImpl(Map<Instrument, Map<Market, TwoWayPrice>> twoWayPriceManager) {
        this.twoWayPriceManager = twoWayPriceManager;
    }

    public TwoWayPrice applyMarketUpdate(final MarketUpdate twoWayMarketPrice) {
        Market market = twoWayMarketPrice.getMarket();
        Instrument instrument = twoWayMarketPrice.getTwoWayPrice().getInstrument();
        TwoWayPrice twoWayPrice = twoWayMarketPrice.getTwoWayPrice();

        //check the existence of instrument
        if (twoWayPriceManager.containsKey(instrument)) {
            Map<Market, TwoWayPrice> twoWayPriceAtMarket = twoWayPriceManager.get(instrument);
            //replace the previous one with the most recent one
            twoWayPriceAtMarket.put(market, twoWayPrice);
            return updateTwoWayPrice(instrument, twoWayPriceAtMarket.values());
        } else {
            //add a new item
            Map<Market, TwoWayPrice> twoWayPriceOnMarket = new HashMap<>();
            twoWayPriceOnMarket.put(market, twoWayPrice);
            twoWayPriceManager.put(instrument, twoWayPriceOnMarket);
            return twoWayPrice;
        }


    }

    @NotNull
    private TwoWayPrice updateTwoWayPrice(Instrument instrument, Collection<TwoWayPrice> twoWayPrices) {
        Double sumOfbidPrice = 0d;
        Double bidAmount = 0d;
        Double sumOfOfferPrice = 0d;
        Double offerAmount = 0d;

        //aggregate all twoWayPrices for the same instrument including the most recent one
        for (TwoWayPrice twoWayPrice : twoWayPrices) {
            sumOfbidPrice += twoWayPrice.getBidPrice() * twoWayPrice.getBidAmount();
            bidAmount += twoWayPrice.getBidAmount();

            sumOfOfferPrice += twoWayPrice.getOfferPrice() * twoWayPrice.getOfferAmount();
            offerAmount += twoWayPrice.getOfferAmount();
        }

        //figure out the avg price according to the provided formula with the default state
        return new TwoWayPriceImpl(instrument, State.FIRM
                , sumOfbidPrice / bidAmount, bidAmount
                , sumOfOfferPrice / offerAmount, offerAmount);
    }

}
