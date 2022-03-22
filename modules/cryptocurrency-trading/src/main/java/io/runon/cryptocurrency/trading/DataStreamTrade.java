package io.runon.cryptocurrency.trading;

import io.runon.trading.Trade;
/**
 * 거래데이터 받기 (리얼타임)
 * @author macle
 */
public abstract class DataStreamTrade<T extends CryptocurrencyTrade> extends DataStream<T>{

    public DataStreamTrade(String streamId) {
        super(streamId);
    }

    public void addTrade(String cryptocurrencyId, Trade trade){
        lastTime = System.currentTimeMillis();
        T cryptocurrency = computeIfAbsent(cryptocurrencyId);
        cryptocurrency.addTrade(trade);
    }
}
