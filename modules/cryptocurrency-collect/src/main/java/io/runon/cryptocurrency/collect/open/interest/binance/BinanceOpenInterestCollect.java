package io.runon.cryptocurrency.collect.open.interest.binance;

import io.runon.commons.config.Config;
import io.runon.commons.utils.time.Times;
import io.runon.commons.utils.time.YmdUtils;
import io.runon.commons.validation.FileValidation;
import io.runon.commons.validation.NumberNameFileValidation;
import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesReTryApis;
import io.runon.cryptocurrency.trading.CryptocurrencyDataPath;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.IdPath;
import io.runon.trading.data.TradingDataPath;
import io.runon.trading.data.file.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 바이낸스 미체결 약정 정보 수집
 * @author macle
 */
@Slf4j
public class BinanceOpenInterestCollect {

    public static final TimeName.Type TIME_NAME_TYPE = TimeName.Type.YEAR_1;
    public static final ZoneId ZONE_ID = TradingTimes.UTC_ZONE_ID;

    private final Map<String, Long> timeMap = new HashMap<>();
    private String [] symbols;

    private long sleepTime = 1000*30;

    private String [] paths;

    public BinanceOpenInterestCollect(){

        String dirPath = CryptocurrencyDataPath.getOpenInterestDirPath();

        setDirPath(dirPath);
        init();

        String [] dirPaths = {
                minute5Path,
                realPath,
                longShortRatioPath+"/top_positions/5m",
                longShortRatioPath+"/top_accounts/5m",
                longShortRatioPath+"/all/5m"
        };

        List<String> pathList = new ArrayList<>();
        for(String symbol : symbols){
            for(String path : dirPaths){
                pathList.add(TradingDataPath.getRelativePath(path + "/" + symbol));
            }
        }

        this.paths = pathList.toArray(new String[0]);
    }

    public BinanceOpenInterestCollect(String dirPath){
        setDirPath(dirPath);
        init();
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    //초기정보 세팅
    private void init(){
        symbols = Config.getConfig("binance.open.interest.symbols","BTCUSDT").split(",");
        FileValidation validation = new NumberNameFileValidation();

        IdPath [] idPaths = {
                new IdPath("5m/", minute5Path)
                , new IdPath("R/", realPath)
                , new IdPath("LSTP/",  longShortRatioPath+"/top_positions/5m")
                , new IdPath("LSTA/",  longShortRatioPath+"/top_accounts/5m")
                , new IdPath("LSA/",  longShortRatioPath+"/all/5m")
        };

        LineOutManager lineOutManager = LineOutManager.getInstance();
        for(String symbol : symbols){
            for(IdPath idPath : idPaths){
                String path = idPath.getPath()+"/" + symbol;

                String dirPath = TradingDataPath.getAbsolutePath(idPath.getPath()+"/" + symbol);
                TimeLineLock timeLineLock = lineOutManager.get(dirPath, PathTimeLine.CSV, TIME_NAME_TYPE);

                log.debug(idPath.getId()+symbol +", " + timeLineLock.getLastTime() +", " + YmdUtils.getYmd(timeLineLock.getLastTime(), ZONE_ID));

                timeMap.put(idPath.getId()+symbol, timeLineLock.getLastTime());
            }
        }
    }

    private String minute5Path;
    private String realPath;
    private String longShortRatioPath;
    private String dirPath;

    private final  LineOutManager lineOutManager = LineOutManager.getInstance();


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setDirPath(String dirPath){
        if(this.dirPath != null && this.dirPath.equals(dirPath)){
            return;
        }
        File file = new File(dirPath);
        if(!dirPath.endsWith("/") || !dirPath.endsWith("\\")){
            dirPath = dirPath + "/";
        }

        minute5Path = dirPath + "5m";
        realPath = dirPath + "real";
        longShortRatioPath =  dirPath + "long_short_ratio";

        if(!file.isDirectory()){
            file.mkdirs();
            new File(minute5Path).mkdirs();
            new File(realPath).mkdirs();
            new File(longShortRatioPath).mkdirs();
            new File(longShortRatioPath+"/top_positions/5m").mkdirs();
            new File(longShortRatioPath+"/top_accounts/5m").mkdirs();
            new File(longShortRatioPath+"/all/5m").mkdirs();
        }else {

            File minuteDir = new File(minute5Path);
            if( !minuteDir.isDirectory()){
                minuteDir.mkdirs();
            }

            File realDir = new File(realPath);
            if( !realDir.isDirectory()){
                realDir.mkdirs();
            }
            File longShortRatioDir = new File(longShortRatioPath);
            if( !longShortRatioDir.isDirectory()){
                longShortRatioDir.mkdirs();
            }
        }
        this.dirPath = dirPath;
    }

    public void collectOpenInterest(){

        for(String symbol: symbols){
            try {

                String dirPath = TradingDataPath.getAbsolutePath(minute5Path +"/"+symbol);
                TimeLineLock timeLineLock = lineOutManager.get(dirPath, PathTimeLine.CSV, TIME_NAME_TYPE);

                String timeKey = "5m/" + symbol;
                long lastTime = timeMap.get(timeKey);

                String jsonValue;
                jsonValue = BinanceFuturesReTryApis.getOpenInterestStatistics(3, Times.MINUTE_1, symbol, "5m", limit5m(lastTime), null, null);
                JSONArray array = new JSONArray(jsonValue);

                if (array.isEmpty()) {
                    continue;
                }

                List<String> lineList = new ArrayList<>();

                long maxTime = 0L;

                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    long time = jsonObject.getLong("timestamp");
                    maxTime = Math.max(time, maxTime);
                    if (time <= lastTime) {
                        continue;
                    }

                    lineList.add(time +"," + jsonObject.getBigDecimal("sumOpenInterest").stripTrailingZeros().toPlainString() +"," + jsonObject.getBigDecimal("sumOpenInterestValue").stripTrailingZeros().toPlainString());
                }

                timeLineLock.add(lineList);

                timeMap.put(timeKey, Math.max(maxTime, lastTime));

                if(sleepTime > 0){
                    Thread.sleep(sleepTime);
                }
            }catch(Exception e){
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public void collectOpenInterestReal(){

        for(String symbol: symbols){
            try{
                String timeKey = "R/" + symbol;
                long lastTime = timeMap.get(timeKey);

                String jsonValue = BinanceFuturesReTryApis.getOpenInterest(3, 30000, symbol);

                if(jsonValue == null || jsonValue.trim().isEmpty()){
                    continue;
                }

                JSONObject object = new JSONObject(jsonValue);

                long time = object.getLong("time");

                if(time <= lastTime){
                    continue;
                }

                String dirPath = TradingDataPath.getAbsolutePath(realPath+"/"+symbol);
                TimeLineLock timeLineLock = lineOutManager.get(dirPath, PathTimeLine.CSV, TIME_NAME_TYPE);

                String [] lines = {time + "," +  object.getBigDecimal("openInterest").stripTrailingZeros().toPlainString()};
                timeLineLock.add(lines);
                timeMap.put(timeKey, Math.max(time, lastTime));

                if(sleepTime > 0){
                    Thread.sleep(sleepTime);
                }

            }catch(Exception e){
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public void collectLongShortRatio(){
        for(String symbol: symbols){
            try{
                String timeKey = "LSA/" + symbol;
                String jsonValue = BinanceFuturesReTryApis.getLongShortRatio(3, Times.MINUTE_1, symbol, "5m", limit5m(timeMap.get(timeKey)), null, null);
                writeLongShortRatio(timeKey,  longShortRatioPath+"/all/5m/"+symbol, jsonValue);
                if(sleepTime > 0){
                    Thread.sleep(sleepTime);
                }
            }catch(Exception e){
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        for(String symbol: symbols){
            try{
                String timeKey = "LSTP/" + symbol;
                String jsonValue = BinanceFuturesReTryApis.getTopLongShortRatioPositions(3, Times.MINUTE_1, symbol, "5m", limit5m(timeMap.get(timeKey)), null, null);
                writeLongShortRatio(timeKey,  longShortRatioPath+"/top_positions/5m/"+symbol, jsonValue);
                if(sleepTime > 0){
                    Thread.sleep(sleepTime);
                }
            }catch(Exception e){
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        for(String symbol: symbols){
            try{
                String timeKey = "LSTA/" + symbol;
                String jsonValue = BinanceFuturesReTryApis.getTopLongShortRatioPositions(3, Times.MINUTE_1, symbol, "5m", limit5m(timeMap.get(timeKey)), null, null);
                writeLongShortRatio(timeKey,  longShortRatioPath+"/top_accounts/5m/"+symbol, jsonValue);
                if(sleepTime > 0){
                    Thread.sleep(sleepTime);
                }
            }catch(Exception e){
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void writeLongShortRatio(String timeKey, String path, String jsonValue){

        long lastTime = timeMap.get(timeKey);

        JSONArray array = new JSONArray(jsonValue);
        if (array.isEmpty()) {
            return;
        }

        long maxTime = 0L;

        List<String> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            long time = jsonObject.getLong("timestamp");
            maxTime = Math.max(time, maxTime);

            if (time <= lastTime) {
                continue;
            }

            list.add(time +"," + jsonObject.getBigDecimal("longShortRatio").stripTrailingZeros().toPlainString() +"," + jsonObject.getBigDecimal("longAccount").stripTrailingZeros().toPlainString() +"," + jsonObject.getBigDecimal("shortAccount").stripTrailingZeros().toPlainString());
        }

        if(list.isEmpty()){
            return ;
        }

        String dirPath = TradingDataPath.getAbsolutePath(path);
        TimeLineLock timeLineLock = lineOutManager.get(dirPath, PathTimeLine.CSV, TIME_NAME_TYPE);

        timeLineLock.add(list);
        timeMap.put(timeKey, Math.max(lastTime, maxTime));
    }

    private int limit5m(long time){
        long limit = (System.currentTimeMillis() - time)/Times.MINUTE_5;
        if(limit > 500){
            limit = 500;
        }else if( limit < 30){
            limit = 30;
        }
        return (int)limit;
    }

    private void write(String path, StringBuilder sb){
        if(FileUtils.isFile(path)){
            FileUtils.fileOutput(sb.toString(), path, true);
        }else{
            FileUtils.fileOutput(sb.substring(1), path, false);
        }
    }

    public String [] getPaths(){
        return paths;
    }


    public static void main(String[] args) {
//        System.out.println( BinanceFuturesApis.getOpenInterestStatistics("BTCUSDT", "5m", 500,null, null));
        BinanceOpenInterestCollect collect = new BinanceOpenInterestCollect();
        collect.collectOpenInterest();
        collect.collectOpenInterestReal();
        collect.collectLongShortRatio();

    }
}
