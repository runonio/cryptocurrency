package io.runon.cryptocurrency.merge.volume.exchanges;

import io.runon.cryptocurrency.exchanges.ftx.FtxTradeStream;
import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * FTX BTC
 * @author macle
 */
public class FtxBtcTradeStream extends FtxTradeStream<BitcoinTrade> {

    private final MergeVolume mergeCandles;

    public FtxBtcTradeStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        //ftx setSubscribeMessage 메시지는 MergeCandlesStarter 에서 설정
    }

    @Override
    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}
