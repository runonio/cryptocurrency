package io.runon.cryptocurrency.trading;

import io.runon.trading.CandleTimes;
import io.runon.trading.technical.analysis.candle.TradeCandle;


/**
 * 캔들저장소
 * @author macle
 */
public class CandleVolumeMergerStore {

    private TradeCandle[] candles = null;
    private long range;
    
    private boolean isCompleteCandle = false;

    /**
     * 완성형 캔들만 사용할지에 대한여부
     * true 인경우 진행중 캔들 정보는 사용하지 않음
     */
    public void setCompleteCandle(boolean completeCandle) {
        isCompleteCandle = completeCandle;
    }

    public void setRange(long range) {
        this.range = range;
    }

    public void setRange(int count){
        //캔들 건수로 범위지정
        range = count*candleVolumeMerge.getCandleTime();
    }

    private final CandleVolumeMerge candleVolumeMerge;
    public CandleVolumeMergerStore (CandleVolumeMerge candleVolumeMerge){
        this.candleVolumeMerge = candleVolumeMerge;
        setRange(500);
    }

    public CandleVolumeMergerStore (CandleVolumeMerge candleVolumeMerge, boolean isCompleteCandle){
        this.candleVolumeMerge = candleVolumeMerge;
        this.isCompleteCandle = isCompleteCandle;
        setRange(500);
    }

    
    private long lastOpenTime = -1;

    /**
     * open시간이 변하지 않았으면 신규 생성하지 않음
     */
    public TradeCandle[] getCandles(long time){
        long openTime = CandleTimes.getOpenTime( candleVolumeMerge.getCandleTime(), time,  candleVolumeMerge.getZoneId());
        if(openTime == lastOpenTime){
            return candles;
        }

        lastOpenTime = openTime;
        candles = newCandles(time, openTime, range);

        return candles;
    }

    public TradeCandle getCandle(long time){
        TradeCandle [] candles = getCandles(time);
        return candles[candles.length-1];
    }

    public TradeCandle getCandle(){
        return candles[candles.length-1];
    }


    /**
     * 신규 생성
     */
    public TradeCandle[] newCandles(long time){
        long openTime = CandleTimes.getOpenTime( candleVolumeMerge.getCandleTime(), time,  candleVolumeMerge.getZoneId());
        return newCandles(time, openTime, range);
    }

    public TradeCandle[] newCandles(long time, long openTime, long range){
        long candleTime = candleVolumeMerge.getCandleTime();
        long endTime = openTime + candleTime;
        long startTime;

        if(isCompleteCandle && endTime > time){
            endTime = openTime;
        }

        startTime = endTime - range;

        return candleVolumeMerge.load(startTime, endTime);
    }

    public long getLastOpenTime() {
        return lastOpenTime;
    }
}
