package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.commons.config.Config;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.time.Times;
import io.runon.cryptocurrency.trading.CandleOut;
import io.runon.trading.CandleTimes;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 바이낸스 캔들 데이터
 * @author macle
 */
@Slf4j
public class BinanceSpotCandleOut extends CandleOut {


    public BinanceSpotCandleOut(){
        outDirPath =  Config.getConfig("cryptocurrency.spot.candle.dir.path","data/cryptocurrency/futures/candle");
    }

    @Override
    public String[] getAllSymbols() {
        String jsonValue = BinanceExchange.getTickers();
        JSONArray jsonArray = new JSONArray(jsonValue);

        String [] allSymbols = new String[jsonArray.length()];

        int length = jsonArray.length();
        for (int i = 0; i < length ; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            allSymbols[i] = jsonObject.getString("symbol");
        }
        return allSymbols;
    }

    private int tryMaxCount =10;

    public void setTryMaxCount(int tryMaxCount) {
        this.tryMaxCount = tryMaxCount;
    }

    public void out(){
        log.info("symbol length: " + symbols.length);
        for(String symbol : symbols){
            for(long candleTime : candleTimes){
                int tryCount = 0;

                for(;;){
                    if(tryCount >= tryMaxCount){
                        log.error("symbol try over error : " + symbol + ", interval: " + CandleTimes.getInterval(candleTime)+ ", try count: " + tryCount);
                        break;
                    }

                    try {
                        log.info("start symbol: " + symbol + ", interval: " + CandleTimes.getInterval(candleTime) +", try count: " + ++tryCount);
                        BinanceCandle.csvNext(BinanceCandle.CANDLE, symbol, candleTime, zoneId, outDirPath, startOpenTime);
                        break;
                    }catch (com.seomse.commons.exception.ConnectRuntimeException e){

                        if(tryCount > 5){
                            try{//noinspection BusyWait
                                Thread.sleep(Times.MINUTE_15);}catch (Exception ignore){}
                        }else{
                            try{//noinspection BusyWait
                                Thread.sleep(Times.MINUTE_5);}catch (Exception ignore){}
                        }

                    }catch (Exception e){
                        log.error(ExceptionUtil.getStackTrace(e));
                        break;
                    }
                }

            }
        }
    }

}
