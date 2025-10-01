package io.runon.cryptocurrency.merge.volume.exchanges.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesCandleStream;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.merge.volume.PriceFuturesCandle;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * 바이낸스선물 BTC (BUSD)
 * @author macle
 */
public class BinanceFuturesBtcCandleStream extends BinanceFuturesCandleStream<PriceFuturesCandle> {

    private final MergeVolume mergeCandles;

    /**
     * @param streamId 아이디 (자유지정, 중복안됨)
     */
    public BinanceFuturesBtcCandleStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        this.setSubscribeMessage("{\"method\":\"SUBSCRIBE\",\"id\":1,\"params\":[\"btcusdt@kline_1d\"]}");
    }

    @Override
    public PriceFuturesCandle newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new PriceFuturesCandle(marketSymbol.getId(), marketSymbol.getMarket(), mergeCandles);
    }
}
