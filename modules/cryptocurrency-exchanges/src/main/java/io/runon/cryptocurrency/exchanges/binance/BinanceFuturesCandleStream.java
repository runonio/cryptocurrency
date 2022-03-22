package io.runon.cryptocurrency.exchanges.binance;

import io.runon.cryptocurrency.trading.CryptocurrencyCandle;

/**
 * 실시간 캔들정보 얻기 (선물거래소)
 * https://binance-docs.github.io/apidocs/futures/en/#kline-candlestick-streams
 * @author macle
 */
public abstract class BinanceFuturesCandleStream<T extends CryptocurrencyCandle> extends BinanceCandleStream<T>{

    /**
     * @param streamId 아이디 (자유지정, 중복안됨)
     */
    public BinanceFuturesCandleStream(String streamId) {
        super(streamId);
        wssAddress = "wss://fstream.binance.com/ws";
    }
}