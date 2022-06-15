package example.binance;

import com.seomse.commons.utils.time.Times;
import com.seomse.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.exchanges.binance.BinanceCandle;

import java.time.ZoneId;

/**
 * 바이낸스 캔들 출력 예제
 * 마지막 캔들정보가 오차되는 문제 해결
 * @author macle
 */
public class BinanceCandleCandleSplitNextOutExample {

    public static void main(String[] args) {
        BinanceCandle.csvNext(BinanceCandle.FUTURES_CANDLE, "yyyyMM", ZoneId.of("Asia/Seoul"), "data/candle","BTCUSDT", Times.MINUTE_1);

    }
}
