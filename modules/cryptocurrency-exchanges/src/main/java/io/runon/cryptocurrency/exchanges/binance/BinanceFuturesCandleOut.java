package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.commons.exception.IORuntimeException;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.time.Times;
import io.runon.cryptocurrency.trading.CandleOut;
import io.runon.cryptocurrency.trading.CryptocurrencyDataPath;
import io.runon.trading.TradingTimes;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

/**
 * 바이낸스 캔들 데이터
 * @author macle
 */
@Slf4j
public class BinanceFuturesCandleOut  extends CandleOut {

    private long sleepTime = 300L;

    public BinanceFuturesCandleOut(){
        outDirPath = CryptocurrencyDataPath.getFuturesCandleDirPath();
    }
    @Override
    public String[] getAllSymbols() {
        return BinanceFuturesApis.getAllSymbols();
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
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
                        log.error("symbol try over error : " + symbol + ", interval: " + TradingTimes.getInterval(candleTime)+ ", try count: " + tryCount);
                        break;
                    }

                    try {
                        log.info("start symbol: " + symbol + ", interval: " + TradingTimes.getInterval(candleTime) +", try count: " + ++tryCount);
                        BinanceCandle.csvNext(BinanceCandle.FUTURES_CANDLE, symbol, candleTime, outDirPath, startOpenTime, sleepTime);
                        break;
                    }catch (IORuntimeException | JSONException e){

                        if(tryCount > 10){
                            log.error("candle out error symbol: " + symbol + ", interval: " + TradingTimes.getInterval(candleTime) +", try count: " +tryCount);
                            break;
                        }

                        if(tryCount > 5){
                            try{//noinspection BusyWait
                                Thread.sleep(Times.MINUTE_15);}catch (Exception ignore){}
                        }else{
                            try{//noinspection BusyWait
                                Thread.sleep(Times.MINUTE_5);}catch (Exception ignore){}
                        }

                    } catch (Exception e){
                        log.error(ExceptionUtil.getStackTrace(e));
                        break;
                    }
                }

            }
        }
    }

}
