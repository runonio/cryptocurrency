package example.binance;

import com.seomse.commons.utils.time.Times;
import com.seomse.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.exchanges.binance.BinanceCandle;

/**
 * 바이낸스 캔들 출력 예제
 * @author macle
 */
public class BinanceCandleExample {

    public static void main(String[] args) {
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_1.csv","BTCUSDT", "1m", YmdUtil.getTime("20220101"), null, 1500);
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_3.csv","BTCUSDT", "3m", YmdUtil.getTime("20220101"), null, 1500);
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_5.csv","BTCUSDT", "5m", YmdUtil.getTime("20220101"), null, 1500);
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_15.csv","BTCUSDT", "15m", YmdUtil.getTime("20220101"), null, 1500);
//        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_30.csv","BTCUSDT", "30m", YmdUtil.getTime("20220101"), null, 1500);


        BinanceCandle.csv(BinanceCandle.FUTURES_CANDLE, "candle_15.csv", "BTCUSDT", Times.MINUTE_15, YmdUtil.getTime("20220101"), 100000 );
    }
}
