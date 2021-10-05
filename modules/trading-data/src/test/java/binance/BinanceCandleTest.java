package binance;

import io.runon.crypto.currency.trading.data.binance.BinanceCandle;

/**
 * 바이낸스 캔들 데이터 내리기
 * @author macle
 */
public class BinanceCandleTest {
    
    public static void main(String[] args) {
        BinanceCandle.candleOut("Binance_1m.csv", "BTCUSDT", "1m", 1633326360000L, null , null);
    }

}
