package io.runon.cryptocurrency.merge.volume.exchanges.okx;

import io.runon.cryptocurrency.exchanges.UsdVolumeConverter;
import io.runon.cryptocurrency.exchanges.okx.OkxExchange;
import io.runon.cryptocurrency.exchanges.okx.OkxTradeStream;
import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
import io.runon.cryptocurrency.merge.volume.MergeVolume;
import io.runon.cryptocurrency.trading.MarketSymbol;
/**
 * Okx 선물거래소
 * @author macle
 */
public class OkxFuturesBtcTradeStream extends OkxTradeStream<BitcoinTrade> {

    private final MergeVolume mergeCandles;

    String [] ids;

    public OkxFuturesBtcTradeStream(String streamId, MergeVolume mergeCandles) {
        super(streamId);
        this.mergeCandles = mergeCandles;
        //선물 모든종목 가져오기
        ids = OkxExchange.getIds("BTC","FUTURES");
        setSubscribeMessage(OkxExchange.getTradeSubscribeMessage(ids));
        setConverter(new UsdVolumeConverter());
    }

    @Override
    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
        return new BitcoinTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
    }
}
