package io.runon.cryptocurrency.merge.volume.exchanges.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceCandleStream;
import io.runon.cryptocurrency.merge.volume.BitcoinCandle;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;
/**
 * 바이낸스 BTC (USDT)
 * @author macle
 */
public class BinanceBtcUsdtCandleStream extends BinanceCandleStream<BitcoinCandle> {

    private final MergeVolume mergeCandles;

    /**
     * @param streamId 아이디 (자유지정, 중복안됨)
     */
    public BinanceBtcUsdtCandleStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        this.setSubscribeMessage("{\"method\":\"SUBSCRIBE\",\"id\":1,\"params\":[\"btcusdt@kline_1d\"]}");
    }

    @Override
    public BitcoinCandle newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinCandle(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}