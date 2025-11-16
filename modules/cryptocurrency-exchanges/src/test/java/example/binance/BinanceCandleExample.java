package example.binance;

import io.runon.commons.utils.time.Times;
import io.runon.commons.utils.time.YmdUtils;
import io.runon.cryptocurrency.exchanges.binance.BinanceCandle;

/**
 * 바이낸스 캔들 출력 예제
 * @author macle
 */
public class BinanceCandleExample {

    public static void main(String[] args) {
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_1.csv","BTCUSDT", "1m", YmdUtils.getTime("20220101"), null, 1500);
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_3.csv","BTCUSDT", "3m", YmdUtils.getTime("20220101"), null, 1500);
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_5.csv","BTCUSDT", "5m", YmdUtils.getTime("20220101"), null, 1500);
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_15.csv","BTCUSDT", "15m", YmdUtils.getTime("20220101"), null, 1500);
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_30.csv","BTCUSDT", "30m", YmdUtils.getTime("20220101"), null, 1500);


        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_15.csv", "BTCUSDT", Times.MINUTE_15, YmdUtils.getTime("20220101"), 100000 );
    }
}
