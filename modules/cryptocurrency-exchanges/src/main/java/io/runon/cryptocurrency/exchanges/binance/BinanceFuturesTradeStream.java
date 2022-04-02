package io.runon.cryptocurrency.exchanges.binance;

import io.runon.cryptocurrency.trading.CryptocurrencyTrade;

/**
 * @author macle
 */
public abstract class BinanceFuturesTradeStream <T extends CryptocurrencyTrade> extends BinanceTradeStream<T>{

    /**
     * @param streamId 아이디 (자유지정, 중복안됨)
     */
    public BinanceFuturesTradeStream(String streamId) {
        super(streamId);
        wssAddress = "wss://fstream.binance.com/ws";
    }
}