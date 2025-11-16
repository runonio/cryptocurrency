package io.runon.cryptocurrency.collect.starter;

import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtils;
import io.runon.commons.utils.time.Times;
import io.runon.commons.utils.time.YmdUtils;
import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;
import io.runon.cryptocurrency.exchanges.binance.BinanceSpotCandleOut;
import io.runon.trading.TradingTimes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author macle
 */
@Slf4j
public class BinanceSpotCandleCollectService extends Service {
    private final String [] markets = {"USDT","BUSD"};
    private final String [] intervals = {"1m","5m","15m","1h","2h","4h","6h","1d"};

    private final long startTime = YmdUtils.getTime("20000101", TradingTimes.UTC_ZONE_ID);

    public BinanceSpotCandleCollectService(){
        setDelayStartTime(5000L);
        setSleepTime(Times.HOUR_2);
        setState(State.START);
    }

    @Override
    public void work() {
        try{
            BinanceSpotCandleOut candleOut = new BinanceSpotCandleOut();
            candleOut.setSymbolsMarket(markets, BinanceExchange.getSymbolRankingMap());
            candleOut.setIntervals(intervals);
            candleOut.setStartOpenTime(startTime);
            candleOut.out();
        }catch(Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
