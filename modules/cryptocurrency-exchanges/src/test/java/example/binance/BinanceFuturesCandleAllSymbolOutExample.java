package example.binance;

import io.runon.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;
import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesCandleOut;
import io.runon.trading.TradingTimes;

/**
 * 바이낸스 선물 캔들 데이터 전종목 내리기
 * 시장지표를 구해서 활용할때 사용
 * @author macle
 */
public class BinanceFuturesCandleAllSymbolOutExample {
    public static void main(String[] args) {


        long startTime = YmdUtil.getTime("20180101", TradingTimes.UTC_ZONE_ID);

        String [] markets = {"USDT","BUSD"};
        String [] intervals = {"1m","5m","15m","1h","4h","6h","1d"};

        BinanceFuturesCandleOut candleOut = new BinanceFuturesCandleOut();
        candleOut.setSymbolsMarket(markets, BinanceExchange.getSymbolRankingMap());
        candleOut.setIntervals(intervals);
        candleOut.setStartOpenTime(startTime);

        candleOut.out();
    }
}
