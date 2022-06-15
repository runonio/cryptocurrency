package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.commons.exception.IORuntimeException;
import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.string.Check;
import com.seomse.crawling.core.http.HttpUrl;
import io.runon.trading.CandleTimes;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import org.json.JSONArray;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 바이낸스 캔들 데이터
 * https://github.com/binance/binance-spot-api-docs/blob/master/rest-api.md
 * https://github.com/binance/binance-spot-api-docs/blob/master/rest-api.md#klinecandlestick-data
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
    public static final String FUTURES_CANDLE = "https://fapi.binance.com/fapi/v1/klines?symbol=%s&interval=%s";

    /**
     *
     * 캔들 데이터를 지정한 파일경로로 추출함 (csv)
     * 파일은 덮어쓰지 않고 기존파일에 붙여서 내용이 생성됨
     * 캔들시작시간(밀리초 유닉스타임)[0],종가[1],시가[2],고가[3],저가[4],직전가[5],거래량[6],거래대금[7],거래횟수[8],매수거래량[9],매수거래대금[10]
     * @param url 바이낸스 현물, 혹은 선물
     * @param outPath 필수 파일 생성경로
     * @param symbol 필수 BTCUSDT, ETHUSDT ...
     * @param interval 필수 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
     * @param startTime null 가능 설정하지 않으면 최신값 (unix time)
     * @param endTime null 가능 설정하지 않으면 최신값 (unix time)
     * @param limit null 가능 default 500 max 1000
     */
    public static void csv(String url, String outPath, String symbol, String interval, Long startTime, Long endTime, Integer limit){

        StringBuilder sb = new StringBuilder();

        JSONArray array = new JSONArray(jsonArray(url, symbol, interval, startTime,endTime, limit));
        int length = array.length();

        for (int i = 0; i < length ; i++) {
            JSONArray data = array.getJSONArray(i);
            sb.append("\n").append(getCsv(data));
        }

        if(FileUtil.isFile(outPath)){
            FileUtil.fileOutput(sb.toString(),outPath, true);
        }else{
            FileUtil.fileOutput(sb.substring(1),outPath, false);
        }
    }

    public static String getCsv(JSONArray data){
        return data.getLong(0) +
                "," + data.getString(4) +
                "," + data.getString(1) +
                "," + data.getString(2) +
                "," + data.getString(3) +
                "," + data.getString(1) +
                "," + data.getString(5) +
                "," + data.getString(7) +
                "," + data.getInt(8) +
                "," + data.getString(9) +
                "," + data.getString(10);
    }

    /**
     * 1000개가 넘는 캔들을 내릴때 반복해서 사용
     * @param url 바이낸스 현물, 혹은 선물
     * @param outPath 필수 파일 생성경로
     * @param symbol 필수 BTCUSDT, ETHUSDT ...
     * @param time unix time
     * @param startTime 시작시간 필수 unix time
     * @param count 필수값 원하는 건수 만큼 건수가 현재시간을 초과할경우 최근시간까지 내림
     */
    @SuppressWarnings("BusyWait")
    public static void csv(String url, String outPath, String symbol, long time, long startTime, int count){
        int total = 0;

        String interval = CandleTimes.getInterval(time);

        outer:
        for(;;) {
            JSONArray array = new JSONArray(jsonArray(url, symbol, interval, startTime, null, 1000));
            int length = array.length();

            if(length == 0){
                break;
            }
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < length; i++) {

                JSONArray data = array.getJSONArray(i);
                long nextTime = data.getLong(0)+ time;

                if(startTime == nextTime){
                    break outer;
                }
                sb.append("\n").append(getCsv(data));
                total++;

                startTime = nextTime;

                if(total >= count){
                    if(FileUtil.isFile(outPath)){
                        FileUtil.fileOutput(sb.toString(),outPath, true);
                    }else{
                        FileUtil.fileOutput(sb.substring(1),outPath, false);
                    }
                    break outer;
                }
            }

            if(FileUtil.isFile(outPath)){
                FileUtil.fileOutput(sb.toString(),outPath, true);
            }else{
                FileUtil.fileOutput(sb.substring(1),outPath, false);
            }
//            long last
            //너무 잦은 호출을 하면 차단당할걸 염두해서 sleep 설정
            try{Thread.sleep(300);}catch(Exception ignore){}
        }
    }

    /**
     * 파일을 나누어서 저장한다
     * 마지막 캔들정보는
     * @param url 바이낸스 현물, 혹은 선물
     * @param timeFormat 날짜형식 (전부숫자로만)
     * @param zoneId 타임존
     * @param outDirPath 파일 디렉토리 경로
     * @param symbol 암호화폐 심볼
     * @param time 시간갭 (1분 3분 5분) 유닉스 타임
     */
    public static void csvNext(String url, String timeFormat, ZoneId zoneId, String outDirPath, String symbol, long time){

        File [] files = new File(outDirPath).listFiles();
        if(files == null){
            throw new IllegalArgumentException("file length 0 path check: " + outDirPath);
        }

        if(files.length == 0){
            throw new IllegalArgumentException("file length 0 path check: " + outDirPath);
        }

        List<File> list = new ArrayList<>();

        for(File file : files){
            if(file.isDirectory()){
                continue;
            }
            if(Check.isNumber(file.getName())){
                list.add(file);
            }
        }

        files = list.toArray(new File[0]);
        FileUtil.sortToNameLong(files, false);

        File lastFile = files[0];
        String lastLine = FileUtil.getLastTextLine(lastFile);
        int index = lastLine.indexOf(',');
        long startOpenTime = Long.parseLong(lastLine.substring(0, index));
        csvSplit(url, timeFormat, zoneId, outDirPath, symbol, time, startOpenTime);
    }

    //파일별로 나누어서 출력할때
    /**
     * 파일별로 나누어서 출력할때
     * 한파일에 너무 많은 파일이 기록되는 경우를 방지
     * 년 ,년월, 년월일, 년월일시  등으로 활용
     * 단 전부 숫자로만 활용할것
     * @param url 바이낸스 현물, 혹은 선물
     * @param timeFormat 날짜형식 (전부숫자로만)
     * @param zoneId 타임존
     * @param outDirPath 파일 디렉토리 경로
     * @param symbol 암호화폐 심볼
     * @param time 시간갭 (1분 3분 5분) 유닉스 타임
     * @param startOpenTime 시작 오픈 시간
     */
    @SuppressWarnings("BusyWait")
    public static void csvSplit(String url, String timeFormat, ZoneId zoneId, String outDirPath, String symbol, long time, long startOpenTime){

        String interval = CandleTimes.getInterval(time);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat).withZone(zoneId);

        if(!outDirPath.endsWith("/") || !outDirPath.endsWith("\\") ){
            outDirPath = outDirPath +"/";
        }

        StringBuilder sb = null;
        String lastOutPath = null;

        outer:
        for(;;) {
            JSONArray array = new JSONArray(jsonArray(url, symbol, interval, startOpenTime, null, 1000));
            int length = array.length();

            if(length == 0){
                break;
            }

            for (int i = 0; i < length; i++) {
                JSONArray data = array.getJSONArray(i);

                long openTime = data.getLong(0);
                long nextTime = openTime + time;

                if(startOpenTime == nextTime){
                    break outer;
                }

                Instant instant = Instant.ofEpochMilli(openTime);
                String outPath = outDirPath + formatter.format(instant);

                if(lastOutPath == null || !lastOutPath.equals(outPath)){

                    if(sb != null){


                        FileUtil.fileOutput(sb.toString(), lastOutPath, false);
                        sb.setLength(0);
                    }

                    lastOutPath = outPath;
                    sb = null;
                }

                if(sb == null){
                    sb = new StringBuilder();

                    List<String> lineList = Collections.emptyList();
                    if(FileUtil.isFile(outPath)){
                        lineList = FileUtil.getFileContentsList(new File(outPath), "UTF-8");
                    }

                    int size = lineList.size();

                    if(size == 0){
                        sb.append(getCsv(data));
                    }else{

                        String line = lineList.get(0);
                        int index = line.indexOf(',');
                        long csvOpenTime = Long.parseLong(line.substring(0, index));

                        if (csvOpenTime >= openTime) {
                            //파일에 내용을 전부 다시 써야함
                            sb.append(getCsv(data));
                        }else{
                            sb.append(line);
                            for (int j = 1; j < size ; j++) {
                                line = lineList.get(j);
                                index = line.indexOf(',');
                                csvOpenTime = Long.parseLong(line.substring(0, index));
                                if (csvOpenTime >= openTime) {
                                    break;
                                }
                                sb.append("\n").append(line);
                            }
                            sb.append("\n").append(getCsv(data));
                        }

                        lineList.clear();
                    }
                }else{
                    sb.append("\n").append(getCsv(data));
                }
                startOpenTime = nextTime;
            }

            //너무 잦은 호출을 하면 차단당할걸 염두해서 sleep 설정
            try{Thread.sleep(300);}catch(Exception ignore){}
        }

        if(sb != null && sb.length() > 0){
            FileUtil.fileOutput(sb.toString(), lastOutPath, false);
        }
    }

    /**
     *
     * @param url 바이낸스 현물, 혹은 선물
     * @param symbol 필수 BTCUSDT, ETHUSDT ...
     * @param interval 필수 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
     * @param startTime null 가능 설정하지 않으면 최신값 (unix time)
     * @param endTime null 가능 설정하지 않으면 최신값 (unix time)
     * @param limit null 가능 default 500 max 1000
     * @return api text json array
     */
    public static String jsonArray(String url, String symbol, String interval, Long startTime, Long endTime, Integer limit){
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(url.formatted(symbol,interval));
        if(startTime != null){
            queryBuilder.append("&startTime=").append(startTime);
        }

        if(endTime != null){
            queryBuilder.append("&endTime=").append(endTime);
        }

        if(limit != null){
            queryBuilder.append("&limit=").append(limit);
        }


        return HttpUrl.get(queryBuilder.toString());
    }

    /**
     *
     * @param url 바이낸스 현물, 혹은 선물
     * @param symbol 필수 BTCUSDT, ETHUSDT ...
     * @param interval 필수 1m, 3m, 5m, 15m, 30m, 1h, 2h, 4h, 6h, 8h, 12h, 1d, 3d, 1w, 1M
     * @param startTime null 가능 설정하지 않으면 최신값 (unix time)
     * @param endTime null 가능 설정하지 않으면 최신값 (unix time)
     * @param limit null 가능 default 500 max 1000
      * @return candles
     */
    public static TradeCandle [] candles(String url, String symbol, String interval, Long startTime, Long endTime, Integer limit){

        JSONArray array = new JSONArray(jsonArray(url, symbol, interval, startTime,endTime, limit));
        TradeCandle [] candles = new TradeCandle[array.length()];

        long candleTime = CandleTimes.getIntervalTime(interval);

        for (int i = 0; i < candles.length ; i++) {
            JSONArray data = array.getJSONArray(i);

            long opeTime = data.getLong(0);

            TradeCandle tradeCandle = new TradeCandle();
            tradeCandle.setOpenTime(opeTime);
            tradeCandle.setCloseTime(opeTime + candleTime);
            tradeCandle.setClose(data.getBigDecimal(4));
            tradeCandle.setOpen(data.getBigDecimal(1));
            tradeCandle.setHigh(data.getBigDecimal(2));
            tradeCandle.setLow(data.getBigDecimal(3));
            tradeCandle.setPrevious(data.getBigDecimal(1));
            tradeCandle.setVolume(data.getBigDecimal(5));
            tradeCandle.setTradingPrice(data.getBigDecimal(7));
            tradeCandle.setTradeCount(data.getInt(8));
            tradeCandle.setBuyVolume(data.getBigDecimal(9));
            tradeCandle.setBuyTradingPrice(data.getBigDecimal(10));

            tradeCandle.setSellVolume();
            tradeCandle.setSellTradingPrice();
            tradeCandle.setChange();

            candles[i] = tradeCandle;
        }
        return candles;
    }


    /**
     * 추출된 파일경로에서 마지막시간부터 현제시간까지 내용을 추가 기록
     * @param url 바이낸스 현물, 혹은 선물
     * @param inPath 필수 추출되었던 파일경로
     * @param symbol 필수 BTCUSDT, ETHUSDT ...
     */
    public static void csv(String url, String inPath, String symbol){
        if(!FileUtil.isFile(inPath)){
            throw new IORuntimeException("file not found : " + inPath);
        }

        long lineCount = FileUtil.getLineCount(inPath);
        if(lineCount < 2){
            throw new IllegalArgumentException("file line count > 2 , count: " + lineCount);
        }

        String line = FileUtil.getLine(inPath, 0);
        long firstTime = Long.parseLong(line.split(",")[0]);
        line = FileUtil.getLine(inPath, 1);
        long time = Long.parseLong(line.split(",")[0]) - firstTime;

        line = FileUtil.getLine(inPath, (int)(lineCount-1));
        long lastTime = Long.parseLong(line.split(",")[0]);

        long startTime = lastTime+time;
        csv(url, inPath, symbol, time, startTime, Integer.MAX_VALUE);
    }


    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd hh:mm:ss").withZone(ZoneId.of("Asia/Seoul"));
        System.out.println(formatter.format(Instant.ofEpochMilli(System.currentTimeMillis())));
    }
}