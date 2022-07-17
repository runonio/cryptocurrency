package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.commons.exception.IORuntimeException;
import com.seomse.crawling.core.http.HttpUrl;

/**
 * 바이낸스 선물 api 에서 활용될 유틸관련 메소드 모음
 * 연결실패에 따른 시도횟수 관련 내용을 추가
 * 수집서비스에서 활용하기 편한 유틸성 메소드
 * 재시도 부분은 오류로 처리하지 않음
 * @author macle
 */
public class BinanceFuturesReTryApis {


    public static String getOpenInterest(int tryCount, long sleepTime, String symbol){

        int check = tryCount-1;
        for (int i = 0; i < check ; i++) {
            try {
                return HttpUrl.get(BinanceFuturesApis.URL + "/fapi/v1/openInterest?symbol=" + symbol);
            }catch (IORuntimeException e){
                try{Thread.sleep(sleepTime);}catch(Exception ignore){}
            }
        }
        return HttpUrl.get(BinanceFuturesApis.URL + "/fapi/v1/openInterest?symbol=" + symbol);
    }


    public static String getOpenInterestStatistics(int tryCount, long sleepTime, String symbol, String period, Integer limit, Long startTime, Long endTime){
        int check = tryCount-1;
        for (int i = 0; i < check ; i++) {
            try {
                return BinanceFuturesApis.get("/futures/data/openInterestHist", symbol, period, limit, startTime, endTime);
            }catch (IORuntimeException e){
                try{Thread.sleep(sleepTime);}catch(Exception ignore){}
            }
        }

        return BinanceFuturesApis.get("/futures/data/openInterestHist", symbol, period, limit, startTime, endTime);
    }

    public static String getLongShortRatio(int tryCount, long sleepTime, String symbol, String period, Integer limit, Long startTime, Long endTime){
        int check = tryCount-1;
        for (int i = 0; i < check ; i++) {
            try {
                return BinanceFuturesApis.get("/futures/data/globalLongShortAccountRatio", symbol, period, limit, startTime, endTime);
            }catch (IORuntimeException e){
                try{Thread.sleep(sleepTime);}catch(Exception ignore){}
            }
        }
        return BinanceFuturesApis.get("/futures/data/globalLongShortAccountRatio", symbol, period, limit, startTime, endTime);
    }

    public static String getTopLongShortRatioAccount(int tryCount, long sleepTime, String symbol, String period, Integer limit, Long startTime, Long endTime){
        int check = tryCount-1;
        for (int i = 0; i < check ; i++) {
            try {
                return BinanceFuturesApis.get("/futures/data/topLongShortAccountRatio", symbol, period, limit, startTime, endTime);
            }catch (IORuntimeException e){
                try{Thread.sleep(sleepTime);}catch(Exception ignore){}
            }
        }
        return BinanceFuturesApis.get("/futures/data/topLongShortAccountRatio", symbol, period, limit, startTime, endTime);
    }

    public static String getTopLongShortRatioPositions(int tryCount, long sleepTime, String symbol, String period, Integer limit, Long startTime, Long endTime){
        int check = tryCount-1;
        for (int i = 0; i < check ; i++) {
            try {
                return BinanceFuturesApis.get("/futures/data/topLongShortPositionRatio", symbol, period, limit, startTime, endTime);
            }catch (IORuntimeException e){
                try{Thread.sleep(sleepTime);}catch(Exception ignore){}
            }
        }

        return BinanceFuturesApis.get("/futures/data/topLongShortPositionRatio", symbol, period, limit, startTime, endTime);
    }

}
