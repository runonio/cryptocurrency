package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.time.Times;
import io.runon.cryptocurrency.trading.SymbolsData;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.file.TimeFiles;
import io.runon.trading.data.file.TimeName;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 호가창 내리기
 * 벡테스팅을 위한 데이터 저장용
 * @author macle
 */
@Slf4j
public abstract class BinanceOrderBookOut extends SymbolsData {

    protected Map<String, Long> lastUpdateMap = new HashMap<>();

    protected TimeName.Type timeNameType =  TimeName.Type.DAY_1;

    public void setTimeNameType(TimeName.Type timeNameType) {
        this.timeNameType = timeNameType;
    }

    public void initUpdateMap(){
        File file = new File(outDirPath);
        if(!file.isDirectory()){
            return;
        }

        File [] symbolDirs = file.listFiles();
        if(symbolDirs == null){
            return;
        }

        for(File symbolDir : symbolDirs){
            if(!symbolDir.isDirectory()){
                continue;
            }

            String line = TimeFiles.getLastLine(symbolDir.getAbsolutePath());
            if(line == null){
                continue;
            }


            try {
                lastUpdateMap.put(symbolDir.getName(), new JSONObject(line).getLong("update_id"));
            }catch(Exception ignore){}
        }
    }

    protected long sleepTime = 1000L;

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    protected int tryMaxCount =10;

    public void setTryMaxCount(int tryMaxCount) {
        this.tryMaxCount = tryMaxCount;
    }

    //asks 매도호가 (매도가격)
    //bids 매수호가 (매수가격)
    @SuppressWarnings("BusyWait")
    public void out(){
        for(String symbol : symbols){
            String symbolDirPath = outDirPath +"/" + symbol;

            //noinspection ResultOfMethodCallIgnored
            new File(symbolDirPath).mkdirs();

            int tryCount = 0;

            for(;;){
                if(tryCount >= tryMaxCount){
                    log.error("symbol try over error : " + symbol +  ", try count: " + tryCount);
                    break;
                }

                try {

                    tryCount++;
                    if(tryCount > 1){
                        log.info("start symbol: " + symbol + ", try count: " + tryCount);
                    }

                    //표준 데이터 형식으로 변경

                    String jsonValue = getJsonValue(symbol);
                    JSONObject binanceOrderBookJson = new JSONObject(jsonValue);
                    long updateId = binanceOrderBookJson.getLong("lastUpdateId");
                    Long lastUpdateId = lastUpdateMap.get(symbol);
                    if(lastUpdateId != null && lastUpdateId == updateId){
                        continue;
                    }

                    String line = BinanceExchange.getOrderBookLine(binanceOrderBookJson);

                    String name = TimeName.getName(System.currentTimeMillis(), timeNameType, TradingTimes.UTC_ZONE_ID);

                    String path = symbolDirPath+"/"+name;

                    File file = new File(path);
                    if(file.isFile()){
                        FileUtil.fileOutput("\n" + line, path, true);
                    }else{
                        FileUtil.fileOutput(line, path, false);
                    }


                    //갱신 아이디 업데이트
                    lastUpdateMap.put(symbol, updateId);

                    try{Thread.sleep(sleepTime);}catch(Exception ignore){}
                    break;
                }catch (com.seomse.commons.exception.IORuntimeException | JSONException e){

                    if(tryCount > 10){
                        log.error("candle out error symbol: " + symbol + ", try count: " +tryCount);
                        break;
                    }

                    if(tryCount > 5){
                        try{Thread.sleep(Times.MINUTE_5);}catch (Exception ignore){}
                    }else{
                        try{Thread.sleep(Times.SECOND_20);}catch (Exception ignore){}
                    }

                }catch (Exception e){
                    log.error(ExceptionUtil.getStackTrace(e));
                    break;
                }

            }

        }
    }

    public abstract String getJsonValue(String symbol);


}
