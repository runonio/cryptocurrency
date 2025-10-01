package io.runon.cryptocurrency.merge.volume.exchanges.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesCandleStream;
import io.runon.cryptocurrency.merge.volume.BitcoinCandle;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * 바이낸스선물 BTC (USDT)
 * @author macle
 */
public class BinanceFuturesBtcBusdCandleStream extends BinanceFuturesCandleStream<BitcoinCandle> {

    private final MergeVolume mergeCandles;

    /**
     * @param streamId 아이디 (자유지정, 중복안됨)
     */
    public BinanceFuturesBtcBusdCandleStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        this.setSubscribeMessage("{\"method\":\"SUBSCRIBE\",\"id\":1,\"params\":[\"btcbusd@kline_1d\"]}");
    }

    @Override
    public BitcoinCandle newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinCandle(marketSymbol.getId(), marketSymbol.getMarket(), mergeCandles);
    }
}