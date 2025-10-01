package io.runon.cryptocurrency.merge.volume.exchanges.kraken;

import io.runon.cryptocurrency.exchanges.kraken.KrakenTradeStream;
import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;
/**
 * kraken XBT
 * @author macle
 */
public class KrakenXbtTradeStream extends KrakenTradeStream<BitcoinTrade> {

    private final MergeVolume mergeCandles;
    public KrakenXbtTradeStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        setSubscribeMessage("{\"event\":\"subscribe\",\"subscription\":{\"name\":\"trade\"},\"pair\":[\"XBT/USD\"]}");
    }

    @Override
    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinTrade(marketSymbol.getId(), marketSymbol.getMarket(), mergeCandles);
    }
}
