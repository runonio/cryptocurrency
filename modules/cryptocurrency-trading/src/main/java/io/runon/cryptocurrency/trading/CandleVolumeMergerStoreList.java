package io.runon.cryptocurrency.trading;

import io.runon.trading.CandleTimes;
import io.runon.trading.technical.analysis.candle.TradeCandle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 캔들저장소
 * 캔들의 변화가 많은경우 캔들을 리스트로 관리
 * Linked List
 * @author macle
 */
public class CandleVolumeMergerStoreList extends CandleVolumeMergerStore{

    private TradeCandle lastCandle;
    private final List<TradeCandle> list = new LinkedList<>();

    public CandleVolumeMergerStoreList(CandleVolumeMerge candleVolumeMerge) {
        super(candleVolumeMerge);
    }

    public CandleVolumeMergerStoreList (CandleVolumeMerge candleVolumeMerge, boolean isCompleteCandle){
        super(candleVolumeMerge, isCompleteCandle);
    }

    public List<TradeCandle> getList(long time){
        long openTime = CandleTimes.getOpenTime( candleVolumeMerge.getCandleTime(), time,  candleVolumeMerge.getZoneId());
        if(!isRealTime && openTime == lastOpenTime){
            return list;
        }

        if(list.size() > 0){
            TradeCandle lastCandle = list.get(list.size()-1);
            long closeTime = openTime + candleVolumeMerge.getCandleTime();
            long startTime = closeTime - range;

            if(lastCandle.getOpenTime() < startTime){
                //범위 초과하였으면 전부 다시생성
                list.clear();
                Collections.addAll(list, newCandles(time, openTime, range));
                return list;
            }

            TradeCandle[] addCandles = candleVolumeMerge.load(lastCandle.getOpenTime(), closeTime);
            if(addCandles[0].getOpenTime() == lastCandle.getOpenTime()){
                list.remove(list.size()-1);
            }
            Collections.addAll(list, addCandles);
        }else{
            Collections.addAll(list, newCandles(time, openTime, range));
        }

        removeOutOfRange(openTime);

        return list;
    }

    /**
     * 캔들추가 
     * 벡테스팅쪽에서 미리 메모리에 올려놓고 추가하는 경우
     * @param candle 추가할 캔들
     */
    public void addCandle(TradeCandle candle){
        if(isRealTime ){
            if(lastOpenTime == candle.getOpenTime()){
                list.remove(list.size()-1);
                list.add(candle);
            }
        }else{
            if( lastOpenTime == candle.getOpenTime() ){
                return;
            }
        }

        list.add(candle);
        removeOutOfRange(candle.getOpenTime());
    }

    private void removeOutOfRange(long openTime){

        long startTime = openTime + candleVolumeMerge.getCandleTime() - range;

        for(;;){
            if(list.size() == 0){
                break;
            }

            TradeCandle candle = list.get(0);
            if(candle.getOpenTime() < startTime){
                list.remove(0);
                continue;
            }
            break;
        }
    }


    @Override
    public TradeCandle[] getCandles(long time){
        return getList(time).toArray(new TradeCandle[0]);
    }
    @Override
    public TradeCandle getCandle(long time){
        TradeCandle [] candles = getCandles(time);
        return candles[candles.length-1];
    }

    @Override
    public TradeCandle getCandle(){
        return lastCandle;
    }

    public List<TradeCandle> getList() {
        return list;
    }
}
