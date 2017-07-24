package com.ubs.dashan.vwap;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class CalculatorMultiThreadImpl implements Calculator {
    private static final int PARTITION_SIZE = 4;
    private Map<Instrument, Map<Market, TwoWayPrice>> twoWayPriceManager;
    private ExecutorService executor;


    public CalculatorMultiThreadImpl(Map<Instrument, Map<Market, TwoWayPrice>> twoWayPriceManager, ExecutorService executor) {
        this.twoWayPriceManager = twoWayPriceManager;
        this.executor = executor;

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
            try {
                return updateTwoWayPrice(instrument, twoWayPriceAtMarket.values());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //add a new item
            Map<Market, TwoWayPrice> twoWayPriceOnMarket = new HashMap<>();
            twoWayPriceOnMarket.put(market, twoWayPrice);
            twoWayPriceManager.put(instrument, twoWayPriceOnMarket);
            return twoWayPrice;
        }

        return null;

    }

    @NotNull
    private TwoWayPrice updateTwoWayPrice(Instrument instrument, Collection<TwoWayPrice> twoWayPrices) throws ExecutionException, InterruptedException {
        if (!twoWayPrices.isEmpty() && twoWayPrices.size() == 1) {
            TwoWayPrice twoWayPrice = twoWayPrices.iterator().next();
            //figure out the avg price according to the provided formula with the default state
            return new TwoWayPriceImpl(instrument, State.FIRM
                    , twoWayPrice.getBidPrice(), twoWayPrice.getBidAmount()
                    , twoWayPrice.getOfferPrice(), twoWayPrice.getOfferAmount());
        } else {
            Collection<Collection<TwoWayPrice>> partitions = partition(twoWayPrices);
            Collection<Future<TwoWayPrice>> results = new ArrayList<>();
            for (Collection<TwoWayPrice> partition : partitions) {
                Callable calculationTask = new CalTask(instrument, partition);
                results.add(executor.submit(calculationTask));
            }

            Collection<TwoWayPrice> aggregatedTwoWayPrice = new ArrayList<>();
            for (Future<TwoWayPrice> result : results) {
                aggregatedTwoWayPrice.add(result.get());
            }
            return updateTwoWayPrice(instrument, aggregatedTwoWayPrice);
        }

    }

    private Collection<Collection<TwoWayPrice>> partition(Collection<TwoWayPrice> twoWayPrices) {
        Collection<Collection<TwoWayPrice>> partions = new ArrayList<>();
        Collection<TwoWayPrice> chunk = new ArrayList<>();
        Iterator<TwoWayPrice> iterator = twoWayPrices.iterator();

        int counter = 0;
        while (iterator.hasNext()) {
            if (counter < PARTITION_SIZE) {
                chunk.add(iterator.next());
            } else {
                partions.add(chunk);
                chunk = new ArrayList<>();
            }
        }
        if (!chunk.isEmpty()) {
            partions.add(chunk);
        }
        return partions;
    }


    public class CalTask implements Callable<TwoWayPrice> {
        private final Instrument instrument;
        private final Collection<TwoWayPrice> twoWayPrices;

        public CalTask(Instrument instrument, Collection<TwoWayPrice> twoWayPrices) {
            this.instrument = instrument;

            this.twoWayPrices = twoWayPrices;
        }

        @Override
        public TwoWayPrice call() throws Exception {
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

            return new TwoWayPriceImpl(instrument, State.FIRM
                    , sumOfbidPrice / bidAmount, bidAmount
                    , sumOfOfferPrice / offerAmount, offerAmount);

        }
    }

}
