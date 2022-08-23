package example.binance;

import com.seomse.commons.utils.time.Times;
import com.seomse.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.exchanges.binance.BinanceCandle;

/**
 * 바이낸스 캔들 출력 예제
 * 마지막 캔들정보가 오차되는 문제 해결
 * @author macle
 */
public class BinanceCandleCandleSplitOutExample {

    public static void main(String[] args) {
        BinanceCandle.csvSplit(BinanceCandle.CANDLE,"BTCUSDT", Times.MINUTE_1, YmdUtil.getTime("20220101"));
    }
}
