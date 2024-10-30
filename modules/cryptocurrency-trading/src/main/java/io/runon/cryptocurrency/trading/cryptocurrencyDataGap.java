package io.runon.cryptocurrency.trading;

import io.runon.trading.TradingTimes;
/**
 * 트레이딩에 사용할 암호화페
 * @author macle
 */
public class cryptocurrencyDataGap {


    //미체결 약정
    public static long getOpenInterest(long time){
        return time;
    }

    //미래참조 편향이 제거된 시간얻기, 실제 내가 사용할 수 잇는 시간정보
    public static long getFearAndGreedTime(long time){
        return TradingTimes.getDayOpenTime(time, TradingTimes.UTC_ZONE_ID);
    }
}