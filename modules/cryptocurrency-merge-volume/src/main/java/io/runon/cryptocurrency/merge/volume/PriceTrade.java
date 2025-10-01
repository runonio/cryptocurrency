package io.runon.cryptocurrency.merge.volume;

import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.trading.Trade;

/**
 * 가격저장용
 * @author macle
 */
public class PriceTrade implements CryptocurrencyTrade {
    long time = System.currentTimeMillis();

    private final String market;
    protected final MergeVolume mergeCandles;
    private final String id;
    public PriceTrade(String id, String market, MergeVolume mergeCandles){
        this.id = id;
        this.market = market;
        this.mergeCandles = mergeCandles;
    }

    @Override
    public void addTrade(Trade trade) {
        time = System.currentTimeMillis();
        mergeCandles.price = trade.getPrice();

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSymbol() {
        return "BTC";
    }

    @Override
    public String getMarket() {
        return market;
    }

    @Override
    public long getLastTime() {
        return time;
    }
}
