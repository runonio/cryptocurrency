//package io.runon.cryptocurrency.merge.volume.exchanges;
//
//import io.runon.cryptocurrency.exchanges.huobi.HuobiTradeStream;
//import io.runon.cryptocurrency.merge.volume.BitcoinTrade;
//import io.runon.cryptocurrency.merge.volume.MergeVolume;
//import io.runon.cryptocurrency.trading.MarketSymbol;
///**
// * 후오비 BTC
// * @author macle
// */
//public class HuobiBtcTradeStream extends HuobiTradeStream<BitcoinTrade> {
//
//    private final MergeVolume mergeCandles;
//
//    public HuobiBtcTradeStream(String streamId, MergeVolume mergeCandles) {
//        super(streamId);
//        this.mergeCandles = mergeCandles;
//        setSubscribeMessage("btcusdt");
//    }
//
//    @Override
//    public BitcoinTrade newCryptocurrency(String cryptocurrencyId) {
//        MarketSymbol marketSymbol = getMarketSymbol(cryptocurrencyId);
//        return new BitcoinTrade(cryptocurrencyId, marketSymbol.getMarket(), mergeCandles);
//    }
//}
