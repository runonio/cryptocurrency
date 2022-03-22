package io.runon.cryptocurrency.exchanges.binance;

import io.runon.cryptocurrency.trading.CryptocurrencyLastCandle;

/**
 * @author macle
 */
public class BinanceFuturesLastCandleStream  extends BinanceFuturesCandleStream<CryptocurrencyLastCandle>{
    /**
     * @param streamId 아이디 (자유지정, 중복안됨)
     */
    public BinanceFuturesLastCandleStream(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyLastCandle newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyLastCandle(getMarketSymbol(cryptocurrencyId));
    }
}
