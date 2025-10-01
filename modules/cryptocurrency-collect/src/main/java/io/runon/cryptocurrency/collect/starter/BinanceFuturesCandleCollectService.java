package io.runon.cryptocurrency.collect.starter;

import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtil;
import io.runon.commons.utils.time.Times;
import io.runon.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;
import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesCandleOut;
import io.runon.trading.TradingTimes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author macle
 */
@Slf4j
public class BinanceFuturesCandleCollectService extends Service {


    private final String [] markets = {"USDT","BUSD"};
    private final String [] intervals = {"1m","5m","15m","1h","2h","4h","6h","1d"};

    private final long startTime = YmdUtil.getTime("20000101", TradingTimes.UTC_ZONE_ID);

    public BinanceFuturesCandleCollectService(){
        setDelayStartTime(5000L);
        setSleepTime(Times.HOUR_6);
        setState(State.START);
    }

    @Override
    public void work() {
        try{
            BinanceFuturesCandleOut candleOut = new BinanceFuturesCandleOut();
            candleOut.setSymbolsMarket(markets, BinanceExchange.getSymbolRankingMap());
            candleOut.setIntervals(intervals);
            candleOut.setStartOpenTime(startTime);
            candleOut.out();
        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }


    }

}
