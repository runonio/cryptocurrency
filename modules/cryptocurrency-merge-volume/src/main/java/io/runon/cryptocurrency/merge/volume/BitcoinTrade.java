package io.runon.cryptocurrency.merge.volume;

import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.trading.Trade;

/**
 * 비트코인 거래정보 데이터
 * @author macle
 */
public class BitcoinTrade implements CryptocurrencyTrade {

    private final String market;
    protected final MergeVolume mergeCandles;
    private long time = System.currentTimeMillis();
    private final String id;
    public BitcoinTrade(String id, String market, MergeVolume mergeCandles){
        this.id = id;
        this.market = market;
        this.mergeCandles = mergeCandles;
    }
    @Override
    public void addTrade(Trade trade) {
        time = System.currentTimeMillis();
        //가격정보 변경 ( 볼륨만 활용함)
        //가격정보는 오차를 줄이기위해 가격을 제공하는 거래소를 지정함
        trade.setPrice(mergeCandles.getPrice());
        mergeCandles.addTrade(trade);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSymbol() {
        return "BTC";
    }

    @Override
    public String getMarket() {
        return market;
    }

    @Override
    public long getLastTime() {
        return time;
    }
}
