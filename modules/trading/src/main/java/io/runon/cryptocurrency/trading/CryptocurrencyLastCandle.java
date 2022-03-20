package io.runon.cryptocurrency.trading;

import io.runon.trading.technical.analysis.candle.TradeCandle;

/**
 * 최종캔들 저장용
 * @author macle
 */
public class CryptocurrencyLastCandle implements CryptocurrencyCandle{
    protected final String id;
    protected final String symbol;
    protected final String market;

    protected long previousTime;
    protected TradeCandle previousCandle = null;

    protected long lastTime;
    protected TradeCandle lastCandle;

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

        if(lastCandle != null && tradeCandle.getOpenTime() != lastCandle.getOpenTime()){
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
