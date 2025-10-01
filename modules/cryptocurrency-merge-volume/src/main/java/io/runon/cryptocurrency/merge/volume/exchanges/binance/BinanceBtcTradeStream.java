package io.runon.cryptocurrency.merge.volume.exchanges.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceTradeStream;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.merge.volume.PriceTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * 바이낸스 BTC trade 가격저장용
 * @author macle
 */
public class BinanceBtcTradeStream extends BinanceTradeStream<PriceTrade> {
    private final MergeVolume mergeCandles;

    public BinanceBtcTradeStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        this.setSubscribeMessage("{\"method\":\"SUBSCRIBE\",\"id\":1,\"params\":[\"btcusdt@aggTrade\"]}");
    }

    @Override
    public PriceTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new PriceTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}
