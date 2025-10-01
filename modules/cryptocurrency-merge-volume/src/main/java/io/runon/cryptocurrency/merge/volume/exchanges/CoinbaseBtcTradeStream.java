package io.runon.cryptocurrency.merge.volume.exchanges;

import io.runon.cryptocurrency.exchanges.coinbase.CoinbaseExchange;
import io.runon.cryptocurrency.exchanges.coinbase.CoinbaseTradeStream;
import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * 코인베이스 BTC
 * @author macle
 */
public class CoinbaseBtcTradeStream extends CoinbaseTradeStream<BitcoinTrade> {
    private final MergeVolume mergeCandles;

    public CoinbaseBtcTradeStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        setSubscribeMessage(CoinbaseExchange.getSubscribeMessage("BTC","USD,USDT"));
    }

    @Override
    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}
