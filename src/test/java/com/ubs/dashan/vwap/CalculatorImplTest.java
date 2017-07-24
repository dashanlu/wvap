package com.ubs.dashan.vwap;


import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;


public class CalculatorImplTest {


    @Test
    public void calculatorShouldReturnTwoWayPriceGivenOneMarketPrice() {
        //given
        Calculator calculator = new CalculatorImpl(new HashMap<>());

        TwoWayPrice twoWayPrice = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 100d, 10d, 100d, 20d);
        MarketUpdate marketUpdate = new MarketUpdateImpl(Market.MARKET0, twoWayPrice);

        //when
        TwoWayPrice wvap = calculator.applyMarketUpdate(marketUpdate);

        //then
        assertTrue("bidPrice", 100 == wvap.getBidPrice());
        assertTrue("bidAmount", 10 == wvap.getBidAmount());
        assertTrue("offerPrice", 100 == wvap.getOfferPrice());
        assertTrue("offerPrice", 20 == wvap.getOfferAmount());

    }

    @Test
    public void calculatorShouldReturnTheRecentPriceGivenTwoPricesForOneMarket() {
        //given
        Calculator calculator = new CalculatorImpl(new HashMap<>());

        TwoWayPrice twoWayPrice1 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 100d, 10d, 100d, 20d);
        TwoWayPrice twoWayPrice2 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 200d, 10d, 200d, 20d);
        MarketUpdate marketUpdate1 = new MarketUpdateImpl(Market.MARKET0, twoWayPrice1);
        MarketUpdate marketUpdate2 = new MarketUpdateImpl(Market.MARKET0, twoWayPrice2);

        //when
        calculator.applyMarketUpdate(marketUpdate1);
        TwoWayPrice wvap = calculator.applyMarketUpdate(marketUpdate2);

        //then
        assertTrue("bidPrice", 200 == wvap.getBidPrice());
        assertTrue("bidAmount", 10 == wvap.getBidAmount());
        assertTrue("offerPrice", 200 == wvap.getOfferPrice());
        assertTrue("offerPrice", 20 == wvap.getOfferAmount());

    }

    @Test
    public void calculatorShouldReturnAvgPriceGivenMultiPricesForTwoMarkets() {
        //given
        Calculator calculator = new CalculatorImpl(new HashMap<>());

        TwoWayPrice twoWayPrice1 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 100d, 10d, 100d, 20d);
        TwoWayPrice twoWayPrice2 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 200d, 10d, 200d, 20d);
        MarketUpdate marketUpdate1 = new MarketUpdateImpl(Market.MARKET0, twoWayPrice1);
        MarketUpdate marketUpdate2 = new MarketUpdateImpl(Market.MARKET1, twoWayPrice2);

        //when
        calculator.applyMarketUpdate(marketUpdate1);
        TwoWayPrice wvap = calculator.applyMarketUpdate(marketUpdate2);

        //then
        assertTrue("bidPrice", 150 == wvap.getBidPrice());
        assertTrue("bidAmount", 20 == wvap.getBidAmount());
        assertTrue("offerPrice", 150 == wvap.getOfferPrice());
        assertTrue("offerPrice", 40 == wvap.getOfferAmount());
    }

    @Test
    public void calculatorShouldReturnAvgPriceGivenMultiPricesForMultiMarkets() {
        //given
        Calculator calculator = new CalculatorImpl(new HashMap<>());

        TwoWayPrice twoWayPrice1 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 100d, 10d, 100d, 10d);
        TwoWayPrice twoWayPrice2 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 200d, 10d, 200d, 20d);
        TwoWayPrice twoWayPrice3 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 200d, 10d, 200d, 40d);
        MarketUpdate marketUpdate1 = new MarketUpdateImpl(Market.MARKET0, twoWayPrice1);
        MarketUpdate marketUpdate2 = new MarketUpdateImpl(Market.MARKET1, twoWayPrice2);
        MarketUpdate marketUpdate3 = new MarketUpdateImpl(Market.MARKET1, twoWayPrice3);

        //when
        calculator.applyMarketUpdate(marketUpdate1);
        calculator.applyMarketUpdate(marketUpdate2);
        TwoWayPrice wvap = calculator.applyMarketUpdate(marketUpdate3);

        //then
        assertTrue("bidPrice", 150 == wvap.getBidPrice());
        assertTrue("bidAmount", 20 == wvap.getBidAmount());
        assertTrue("offerPrice", 180 == wvap.getOfferPrice());
        assertTrue("offerPrice", 50 == wvap.getOfferAmount());
    }

    @Test
    public void calculateShouldReturnAvgPricesGiveMultiPricesForMultiMarketsAndInstruments() {
        //given
        Calculator calculator = new CalculatorImpl(new HashMap<>());

        TwoWayPrice twoWayPrice1 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 100d, 10d, 100d, 10d);
        TwoWayPrice twoWayPrice2 = new TwoWayPriceImpl(Instrument.INSTRUMENT1, State.FIRM
                , 200d, 10d, 200d, 20d);
        TwoWayPrice twoWayPrice3 = new TwoWayPriceImpl(Instrument.INSTRUMENT0, State.FIRM
                , 200d, 10d, 200d, 40d);
        TwoWayPrice twoWayPrice4 = new TwoWayPriceImpl(Instrument.INSTRUMENT1, State.FIRM
                , 200d, 10d, 200d, 40d);

        MarketUpdate marketUpdate1 = new MarketUpdateImpl(Market.MARKET0, twoWayPrice1);
        MarketUpdate marketUpdate2 = new MarketUpdateImpl(Market.MARKET1, twoWayPrice2);
        MarketUpdate marketUpdate3 = new MarketUpdateImpl(Market.MARKET2, twoWayPrice3);
        MarketUpdate marketUpdate4 = new MarketUpdateImpl(Market.MARKET4, twoWayPrice4);

        //when
        calculator.applyMarketUpdate(marketUpdate1);
        calculator.applyMarketUpdate(marketUpdate2);
        TwoWayPrice wvap1 = calculator.applyMarketUpdate(marketUpdate3);
        TwoWayPrice wvap2 = calculator.applyMarketUpdate(marketUpdate4);

        //then
        assertTrue("bidPrice for instrument0", 150 == wvap1.getBidPrice());
        assertTrue("bidAmount for instrument0", 20 == wvap1.getBidAmount());
        assertTrue("offerPrice for instrument0", 180 == wvap1.getOfferPrice());
        assertTrue("offerAmount for instrument0", 50 == wvap1.getOfferAmount());

        assertTrue("bidPrice for instrument1", 200 == wvap2.getBidPrice());
        assertTrue("bidAmount for instrument1", 20 == wvap2.getBidAmount());
        assertTrue("offerPrice for instrument1", 200 == wvap2.getOfferPrice());
        assertTrue("offerAmount for instrument1", 60 == wvap2.getOfferAmount());


    }
}