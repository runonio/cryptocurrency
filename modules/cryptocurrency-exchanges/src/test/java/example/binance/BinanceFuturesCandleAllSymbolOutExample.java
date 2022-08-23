package example.binance;

import com.seomse.commons.config.Config;
import com.seomse.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesCandleOut;
import io.runon.trading.CandleTimes;

/**
 * 바이낸스 선물 캔들 데이터 전종목 내리기
 * 시장지표를 구해서 활용할때 사용
 * @author macle
 */
public class BinanceFuturesCandleAllSymbolOutExample {
    public static void main(String[] args) {

//        Config.setConfig("binance.candle.collect.sleep.time", "2000");
        Config.setConfig("cryptocurrency.futures.candle.dir.path", "D:\\data\\cryptocurrency\\futures\\candle");


        long startTime = YmdUtil.getTime("20180101", CandleTimes.US_STOCK_ZONE_ID);

        String [] markets = {"USDT","BUSD"};
        String [] intervals = {"1m","5m","15m","1h","4h","1d"};

        BinanceFuturesCandleOut candleOut = new BinanceFuturesCandleOut();
        candleOut.setSymbolsMarket(markets);
        candleOut.setIntervals(intervals);
        candleOut.setStartOpenTime(startTime);
        candleOut.setZoneId(CandleTimes.US_STOCK_ZONE_ID);

        candleOut.out();
    }
}
