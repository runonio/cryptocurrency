package io.runon.cryptocurrency.merge.volume;

import io.runon.cryptocurrency.trading.CandleVolumeMerge;
import io.runon.cryptocurrency.trading.CandleVolumeMergerStore;
import io.runon.cryptocurrency.trading.CryptocurrencyDataPath;
import io.runon.trading.TradingTimes;

/**
 * 바이낸스 거래소 거래량 합친정보
 * @author macle
 */
public class BinanceVolumeMerge {

    public static CandleVolumeMergerStore newCandleVolumeMerge(String interval, long range){
        CandleVolumeMergerStore candleVolumeMergerStore = newCandleVolumeMerge(interval);
        candleVolumeMergerStore.setRange(range);
        return candleVolumeMergerStore;
    }

    public static CandleVolumeMergerStore newCandleVolumeMerge(String interval, int rangeCount){
        CandleVolumeMergerStore candleVolumeMergerStore = newCandleVolumeMerge(interval);
        candleVolumeMergerStore.setRange(rangeCount);
        return candleVolumeMergerStore;
    }

    public static CandleVolumeMergerStore newCandleVolumeMerge(String interval){

        CandleVolumeMerge candleVolumeMerge = new CandleVolumeMerge();

        String spotCandleDirPath = CryptocurrencyDataPath.getSpotCandleDirPath();

        String futuresCandleDirPath = CryptocurrencyDataPath.getFuturesCandleDirPath();

        String path = spotCandleDirPath + "/BTCBUSD";
        String [] addPaths = {
                spotCandleDirPath + "/BTCUSDT"
                , futuresCandleDirPath + "/BTCUSDT"
                , futuresCandleDirPath + "/BTCBUSD"
        };

        candleVolumeMerge.setPath(path);
        candleVolumeMerge.setAddPaths(addPaths);
        candleVolumeMerge.setInterval(interval);

        return new CandleVolumeMergerStore(candleVolumeMerge, true);
    }
}
