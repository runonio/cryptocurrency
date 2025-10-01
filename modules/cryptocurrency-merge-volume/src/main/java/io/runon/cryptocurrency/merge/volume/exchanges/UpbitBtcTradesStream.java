package io.runon.cryptocurrency.merge.volume.exchanges;

import io.runon.cryptocurrency.exchanges.upbit.UpbitExchange;
import io.runon.cryptocurrency.exchanges.upbit.UpbitTradeStream;
import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * 업비트 BTC
 * @author macle
 */
public class UpbitBtcTradesStream extends UpbitTradeStream<BitcoinTrade> {

    private final MergeVolume mergeCandles;

    public UpbitBtcTradesStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        //btc정보만 활용
        setSubscribeMessage(UpbitExchange.getSubscribeMessage("BTC","KRW"));
    }

    @Override
    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}
