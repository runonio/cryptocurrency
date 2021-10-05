package io.runon.crypto.currency.trading.data.binance;

import com.seomse.commons.utils.FileUtil;
import com.seomse.crawling.core.http.HttpUrl;
import org.json.JSONArray;

/**
 * 바이낸스 캔들 데이터
 * @author macle
 */
public class BinanceCandle {

//      parameter
//      symbol	STRING	YES
//      interval	ENUM	YES
//      startTime	LONG	NO
//      endTime	LONG	NO
//      limit	INT	NO	Default 500; max 1000.

    //intervals
    //1m
    //3m
    //5m
    //15m
    //30m
    //1h
    //2h
    //4h
    //6h
    //8h
    //12h
    //1d
    //3d
    //1w
    //1M

//response
// [
//  [
//    1499040000000,      // Open time
//    "0.01634790",       // Open
//    "0.80000000",       // High
//    "0.01575800",       // Low
//    "0.01577100",       // Close
//    "148976.11427815",  // Volume
//    1499644799999,      // Close time
//    "2434.19055334",    // Quote asset volume
//    308,                // Number of trades
//    "1756.87402397",    // Taker buy base asset volume
//    "28.46694368",      // Taker buy quote asset volume
//    "17928899.62484339" // Ignore.
//  ]
//]

    public static final String CANDLE = "https://api.binance.com/api/v3/klines?symbol=%s&interval=%s";

    /**
     * 
     * 캔들 데이터를 지정한 파일경로로 추출함
     * 파일은 덮어쓰지 않고 기존파일에 붙여서 내용이 생성됨
     * 
     * intervals
     * 1m
     * 3m
     * 5m
     * 15m
     * 30m
     * 1h
     * 2h
     * 4h
     * 6h
     * 8h
     * 12h
     * 1d
     * 3d
     * 1w
     * 1M
     * @param outPath 필수 파일 생성경로
     * @param symbol 필수 BTCUSDT, ETHUSDT ...
     * @param interval 필수
     * @param startTime null 가능 설정하지 않으면 최신값 (unix time)
     * @param endTime null 가능 설정하지 않으면 최신값 (unix time)
     * @param limit null 가능 default 500 max 1000
     */
    public static void candleOut(String outPath, String symbol, String interval, Long startTime, Long endTime, Integer limit){

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(CANDLE.formatted(symbol,interval));
        if(startTime != null){
            queryBuilder.append("&startTime=").append(startTime);
        }

        if(endTime != null){
            queryBuilder.append("&endTime=").append(startTime);
        }

        if(limit != null){
            queryBuilder.append("&limit=").append(startTime);
        }

        String value = HttpUrl.get(queryBuilder.toString());

        StringBuilder sb = new StringBuilder();

        JSONArray array = new JSONArray(value);
        int length = array.length();

        for (int i = 0; i < length ; i++) {
            JSONArray data = array.getJSONArray(i);
            sb.append("\n").append(data.getLong(0)/1000L)
                    .append(",").append(data.getString(4))
                    .append(",").append(data.getString(1))
                    .append(",").append(data.getString(2))
                    .append(",").append(data.getString(3))
                    .append(",").append(data.getString(1))
                    .append(",").append(data.getString(5))
                    .append(",").append(data.getString(7))
                    .append(",").append(data.getInt(8))
                    .append(",").append(data.getString(9))
                    .append(",").append(data.getString(10))
                    ;

        }

        if(FileUtil.isFile(outPath)){
            FileUtil.fileOutput(sb.toString(),outPath, true);
        }else{
            FileUtil.fileOutput(sb.substring(1),outPath, true);
        }

    }
}
