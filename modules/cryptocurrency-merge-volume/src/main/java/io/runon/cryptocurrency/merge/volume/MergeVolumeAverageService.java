package io.runon.cryptocurrency.merge.volume;

import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtil;
import io.runon.commons.utils.time.Times;
import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesCandleOut;
import io.runon.cryptocurrency.exchanges.binance.BinanceSpotCandleOut;
import io.runon.trading.TradingTimes;
import lombok.extern.slf4j.Slf4j;

/**
 * 거래량 평균 서비스
 * @author macle
 */
@Slf4j
public class MergeVolumeAverageService extends Service {

    private final MergeVolume mergeVolume;

    private final BinanceSpotCandleOut binanceSpotCandleOut;
    private final BinanceFuturesCandleOut binanceFuturesCandleOut;

    public MergeVolumeAverageService(MergeVolume mergeVolume){
        this.mergeVolume = mergeVolume;
        setDelayStartTime(Times.MINUTE_10);
        setSleepTime(Times.MINUTE_10);
        setState(State.START);

        long startTime = System.currentTimeMillis() - Times.DAY_1*930;
        startTime = TradingTimes.getOpenTime(Times.HOUR_1, startTime, TradingTimes.UTC_ZONE_ID);

        String [] markets = {"USDT","BUSD"};
        String [] intervals = {"1h"};
        String [] symbols = {"BTCBUSD","BTCUSDT"};

        binanceSpotCandleOut = new BinanceSpotCandleOut();
        binanceSpotCandleOut.setSymbols(symbols);
        binanceSpotCandleOut.setIntervals(intervals);
        binanceSpotCandleOut.setStartOpenTime(startTime);

        binanceFuturesCandleOut = new BinanceFuturesCandleOut();
        binanceFuturesCandleOut.setSymbols(symbols);
        binanceFuturesCandleOut.setIntervals(intervals);
        binanceFuturesCandleOut.setStartOpenTime(startTime);

    }

    @Override
    public void work() {
        try{
            binanceSpotCandleOut.out();
        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }

        try{
            binanceFuturesCandleOut.out();
        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }

        try{
            mergeVolume.avg();
        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }

    }
}
