package io.runon.cryptocurrency.merge.volume.exchanges;

import io.runon.cryptocurrency.exchanges.bithumb.BithumbTradeStream;
import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * 빗썸 BTC
 * @author macle
 */
public class BithumbBtcTradeStream extends BithumbTradeStream<BitcoinTrade> {
    private final MergeVolume mergeCandles;

    public BithumbBtcTradeStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
    }

    @Override
    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}
