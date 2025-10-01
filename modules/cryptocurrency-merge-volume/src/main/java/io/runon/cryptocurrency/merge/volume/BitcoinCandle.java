package io.runon.cryptocurrency.merge.volume;

import io.runon.cryptocurrency.trading.CryptocurrencyCandle;
import io.runon.trading.Trade;
import io.runon.trading.technical.analysis.candle.TradeCandle;

import java.math.BigDecimal;

/**
 * 비트코인 캔들 데이터
 * @author macle
 */
public class BitcoinCandle implements CryptocurrencyCandle {

    private long time = System.currentTimeMillis();
    private final String market;
    protected final MergeVolume mergeCandles;
    private final String id;
    public BitcoinCandle(String id, String market, MergeVolume mergeCandles){
        this.id = id;
        this.market = market;
        this.mergeCandles = mergeCandles;
    }

    private TradeCandle lastCandle = null;

    //시가 고가가
    //캔들방식일때
    @Override
    public void addCandle(TradeCandle tradeCandle) {
       time = System.currentTimeMillis();

       if(lastCandle == null){
           lastCandle = tradeCandle;
           return;
       }

       // 가격은 프리미엄을 생각하여 시세정보에 영향이 적은 거래소만 등록하게 한다.
       //다른거래소는 등록된 가격을 활용한다,
       BigDecimal price = mergeCandles.getPrice();
       if(price == null){
           return;
       }
       Trade buy = null;
       Trade sell = null;
       int tradingCount;
       if(lastCandle.getOpenTime() == tradeCandle.getOpenTime()){
           //차이를 생성하여 생성
           tradingCount = tradeCandle.getTradeCount() - lastCandle.getTradeCount();
           if(tradeCandle.getBuyVolume().compareTo(lastCandle.getBuyVolume()) > 0){
               buy = new Trade(Trade.Type.BUY, price, tradeCandle.getBuyVolume().subtract(lastCandle.getBuyVolume()), time);
               buy.setAmount(tradeCandle.getBuyAmount().subtract(lastCandle.getBuyAmount()));
           }

           if(tradeCandle.getSellVolume().compareTo(lastCandle.getSellVolume()) > 0){
               sell = new Trade(Trade.Type.SELL, price, tradeCandle.getSellVolume().subtract(lastCandle.getSellVolume()), time);
               sell.setAmount(tradeCandle.getSellAmount().subtract(lastCandle.getSellAmount()));
           }

       }else{
           //다르면 신규정보 생성
           tradingCount = tradeCandle.getTradeCount();
           if(tradeCandle.getBuyVolume() != null) {
               buy = new Trade(Trade.Type.BUY, price, tradeCandle.getBuyVolume(), time);
               buy.setAmount(tradeCandle.getBuyAmount());
           }

           if(tradeCandle.getSellVolume() != null) {
               sell = new Trade(Trade.Type.SELL, price, tradeCandle.getSellVolume(), time);
               sell.setAmount(tradeCandle.getSellAmount());
           }
       }

       lastCandle = tradeCandle;
       if(buy != null && sell != null){
           Trade [] trades = new Trade[2];
           trades[0] = buy;
           trades[1] = sell;
           tradingCount = tradingCount-2;
           if(tradingCount > 0){
               mergeCandles.addTrade(trades, tradingCount);
           }else{
               mergeCandles.addTrade(trades);
           }

           return;
       }

       if(buy != null){
           tradingCount--;
           if(tradingCount > 0){
               mergeCandles.addTrade(buy, tradingCount);
           }else{
               mergeCandles.addTrade(buy);
           }

           return;
       }

       if(sell != null){
           tradingCount--;
           if(tradingCount > 0){
               mergeCandles.addTrade(sell, tradingCount);
           }else{
               mergeCandles.addTrade(sell);
           }

       }

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
