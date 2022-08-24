package example.binance;

import com.seomse.commons.config.Config;
import com.seomse.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;
import io.runon.cryptocurrency.exchanges.binance.BinanceSpotCandleOut;
import io.runon.trading.CandleTimes;

/**
 * 바이낸스 캔들 데이터 전종목 내리기
 * 시장지표를 구해서 활용할때 사용
 * @author macle
 */
public class BinanceSpotCandleAllSymbolOutExample {
    public static void main(String[] args) {

        Config.setConfig("binance.candle.collect.sleep.time", "2000");
        Config.setConfig("cryptocurrency.spot.candle.dir.path", "D:\\data\\cryptocurrency\\spot\\candle");


        long startTime = YmdUtil.getTime("20180101", CandleTimes.US_STOCK_ZONE_ID);

        String [] markets = {"USDT","BUSD"};
        String [] intervals = {"1m","5m","15m","1h","4h","1d"};

        BinanceSpotCandleOut candleOut = new BinanceSpotCandleOut();
        candleOut.setSymbolsMarket(markets, BinanceExchange.getSymbolRankingMap());
        candleOut.setIntervals(intervals);
        candleOut.setStartOpenTime(startTime);
        candleOut.setZoneId(CandleTimes.US_STOCK_ZONE_ID);

        candleOut.out();
    }
}
