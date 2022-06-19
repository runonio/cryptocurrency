package example.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesApis;

/**
 * 최근미체결 약정
 * @author macle
 */
public class BinanceOpenInterestExample {
    public static void main(String[] args) {

        System.out.println(BinanceFuturesApis.getOpenInterest("BTCUSDT"));
        System.out.println(BinanceFuturesApis.getOpenInterestStatistics("BTCUSDT", null, null, null, null));

    }
}
