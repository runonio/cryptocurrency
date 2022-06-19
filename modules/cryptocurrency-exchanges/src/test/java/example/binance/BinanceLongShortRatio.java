package example.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesApis;

/**
 * 롱숏 비율
 * @author macle
 */
public class BinanceLongShortRatio {
    public static void main(String[] args) {
        System.out.println(BinanceFuturesApis.getLongShortRatio("BTCUSDT", null, null, null, null));

        System.out.println(BinanceFuturesApis.getTopLongShortRatioAccount("BTCUSDT", null, null, null, null));
        System.out.println(BinanceFuturesApis.getTopLongShortRatioPositions("BTCUSDT", null, null, null, null));
    }
}
