package io.runon.cryptocurrency.trading;

import io.runon.trading.technical.analysis.candle.TradeCandle;

/**
 * 캔들 받기 (리얼타임)
 * @author macle
 */
public abstract class DataStreamCandle<T extends CryptocurrencyCandle> extends DataStream<T>{
    public DataStreamCandle(String streamId) {
        super(streamId);
    }

    public void addCandle(String cryptocurrencyId, TradeCandle tradeCandle){
        lastTime = System.currentTimeMillis();
        T cryptocurrency = computeIfAbsent(cryptocurrencyId);
        cryptocurrency.addCandle(tradeCandle);
    }
}
