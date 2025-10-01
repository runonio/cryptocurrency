package io.runon.cryptocurrency.merge.volume.exchanges.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceCandleStream;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.merge.volume.PriceCandle;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * 바이낸스 BTC (BUSD)
 * @author macle
 */
public class BinanceBtcCandleStream extends BinanceCandleStream<PriceCandle> {

    private final MergeVolume mergeCandles;

    /**
     * @param streamId 아이디 (자유지정, 중복안됨)
     */
    public BinanceBtcCandleStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        this.setSubscribeMessage("{\"method\":\"SUBSCRIBE\",\"id\":1,\"params\":[\"btcbusd@kline_1d\"]}");
    }

    @Override
    public PriceCandle newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new PriceCandle(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}
