package io.runon.cryptocurrency.merge.volume.exchanges.okx;

import io.runon.cryptocurrency.exchanges.okx.OkxTradeStream;
import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * Okx BTC (현물 usdt)
 * @author macle
 */
public class OkxBtcTradeStream extends OkxTradeStream<BitcoinTrade> {

    private final MergeVolume mergeCandles;
    public OkxBtcTradeStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        setSubscribeMessage("{\"op\":\"subscribe\",\"args\":[{\"channel\":\"trades\",\"instId\":\"BTC-USDT\"}]}");
    }

    @Override
    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}
