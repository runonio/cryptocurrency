package io.runon.cryptocurrency.trading;

import io.runon.trading.technical.analysis.candle.TradeCandle;

/**
 * 최종캔들 저장용
 * @author macle
 */
public class CryptocurrencyLastCandle implements CryptocurrencyCandle{
    private final String id;
    private final String symbol;
    private final String market;


    private long previousTime;
    private TradeCandle previousCandle = null;

    private long lastTime;
    private TradeCandle lastCandle;

    public CryptocurrencyLastCandle(MarketSymbol marketSymbol) {
        this.id = marketSymbol.getId();
        this.market = marketSymbol.getMarket();
        this.symbol = marketSymbol.getSymbol();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getMarket() {
        return market;
    }

    @Override
    public long getLastTime() {
        return lastTime;
    }

    @Override
    public void addCandle(TradeCandle tradeCandle) {
        if(tradeCandle.getOpenTime() != lastCandle.getOpenTime()){
            previousTime = lastTime;
            previousCandle = lastCandle;
        }
        lastTime = System.currentTimeMillis();
        lastCandle = tradeCandle;
    }

    public long getPreviousTime() {
        return previousTime;
    }

    public TradeCandle getPreviousCandle() {
        return previousCandle;
    }

    public TradeCandle getLastCandle() {
        return lastCandle;
    }
}
