package io.runon.cryptocurrency.merge.volume;

import io.runon.trading.technical.analysis.candle.TradeCandle;
/**
 * 시세정보 저장기능이 있는 캔들
 * @author macle
 */
public class PriceCandle extends BitcoinCandle {

    public PriceCandle(String id, String market, MergeVolume mergeCandles) {
        super(id, market, mergeCandles);
    }

    //시가 고가가
    //캔들방식일때
    @Override
    public void addCandle(TradeCandle tradeCandle) {
        mergeCandles.price = tradeCandle.getClose();
        super.addCandle(tradeCandle);
    }

}