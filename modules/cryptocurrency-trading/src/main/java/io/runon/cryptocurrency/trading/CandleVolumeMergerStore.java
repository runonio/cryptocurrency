package io.runon.cryptocurrency.trading;

import io.runon.trading.CandleTimes;
import io.runon.trading.technical.analysis.candle.TaCandles;
import io.runon.trading.technical.analysis.candle.TradeCandle;


/**
 * 캔들저장소
 * 배열복사가 일어나는 방식으로 복사된 배열을 반복해서 사용해 분석할때는 활용도가 높다
 * 추가가 너무 많이 일어나는경우 CandleVolumeMergerStoreList 를 활용한다.
 * @author macle
 */
public class CandleVolumeMergerStore {

    protected TradeCandle[] candles = null;
    protected long range;

    protected boolean isCompleteCandle = false;

    protected boolean isRealTime = false;

    /**
     * 실시간 반응여부설정
     * 봇 운영, 혹은 운영환경 테스트의경우 true 기존에 있는 데이터로 테스트하는경우 false로 설정하는것을 추천한다.
     * true 로 설정하면 메모리가 더 자주 갱신된다
     * false 일 경우 1분단위로 갱신한다.
     * @param realTime default false
     */
    public void setRealTime(boolean realTime) {
        isRealTime = realTime;
    }

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

    protected final CandleVolumeMerge candleVolumeMerge;
    public CandleVolumeMergerStore (CandleVolumeMerge candleVolumeMerge){
        this.candleVolumeMerge = candleVolumeMerge;
        setRange(500);
    }

    public CandleVolumeMergerStore (CandleVolumeMerge candleVolumeMerge, boolean isCompleteCandle){
        this.candleVolumeMerge = candleVolumeMerge;
        this.isCompleteCandle = isCompleteCandle;
        setRange(500);
    }


    protected long lastOpenTime = -1;

    /**
     * open시간이 변하지 않았으면 신규 생성하지 않음
     */
    public TradeCandle[] getCandles(long time){
        long openTime = CandleTimes.getOpenTime( candleVolumeMerge.getCandleTime(), time,  candleVolumeMerge.getZoneId());
        if(!isRealTime && openTime == lastOpenTime){
            return candles;
        }

        lastOpenTime = openTime;

        //캔들 누적
        //이전 캔들정보 활용해서 이후 정보를 생성할 것
        if(candles != null ){

            TradeCandle [] candles = this.candles;
            //시작 시간은 캔들의 끝시간
            TradeCandle lastCandle = candles[candles.length-1];
            long closeTime = openTime + candleVolumeMerge.getCandleTime();
            long startTime = closeTime - range;

            if(lastCandle.getOpenTime() < startTime){
                //범위 초과하였으면 전부 다시생성
                this.candles = newCandles(time, openTime, range);
                return this.candles;
            }


            int idx = TaCandles.getOpenTimeIndex(candles, candleVolumeMerge.getCandleTime(), startTime);

            if(idx == -1){
                this.candles = newCandles(time, openTime, range);
                return this.candles;
            }

            int length = candles.length - idx -1;

            //아닌경우 생성해서 이어 붙이기
            //단 생성해서 붙일떄 마지막 캔들을 다시 가져온다
            //캔들이 종료되지 않은경우 마지막 캔들 변경을 하는게 좋다.
            TradeCandle[] addCandles = candleVolumeMerge.load(lastCandle.getOpenTime(), closeTime);

            TradeCandle [] newCandles =  new TradeCandle[length + addCandles.length];

            int newIndex = 0;
            for (int i = idx; i < candles.length -1 ; i++) {
                newCandles[newIndex++] = candles[i];
            }

            for (TradeCandle addCandle : addCandles) {
                newCandles[newIndex++] = addCandle;
            }

            this.candles = newCandles;

        }else{
            this.candles = newCandles(time, openTime, range);
        }

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


    public void setNewCandle(long time){
        long openTime = CandleTimes.getOpenTime( candleVolumeMerge.getCandleTime(), time,  candleVolumeMerge.getZoneId());

        //캔들정보 갱신
        //저장건수가 변경되거나 할때 다시 설정함
        lastOpenTime = openTime;
        candles = newCandles(time, openTime, range);
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

    public TradeCandle[] newCandles(long startTime, long endTime){
        return candleVolumeMerge.load(startTime, endTime);
    }


    public long getLastOpenTime() {
        return lastOpenTime;
    }
}
