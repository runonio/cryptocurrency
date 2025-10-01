package io.runon.cryptocurrency.merge.volume.exchanges.kraken;

import io.runon.cryptocurrency.exchanges.UsdVolumeConverter;
import io.runon.cryptocurrency.exchanges.kraken.KrakenFuturesTradeStream;
import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;
/**
 * kraken 선물거래소 XBT
 * @author macle
 */
public class KrakenFuturesXbtTradeStream  extends KrakenFuturesTradeStream<BitcoinTrade> {

    private final MergeVolume mergeCandles;
    public KrakenFuturesXbtTradeStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        setSubscribeMessage("{\"event\":\"subscribe\",\"feed\":\"trade\",\"product_ids\":[\"PI_XBTUSD\"]}");
        setConverter(new UsdVolumeConverter());
    }

    @Override
    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}

