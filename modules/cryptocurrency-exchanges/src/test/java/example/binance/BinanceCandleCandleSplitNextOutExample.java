package example.binance;

import com.seomse.commons.utils.time.Times;
import com.seomse.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.exchanges.binance.BinanceCandle;
import io.runon.cryptocurrency.exchanges.binance.BinanceSpotCandleOut;

/**
 * 바이낸스 캔들 전체종목 내리기
 * @author macle
 */
public class BinanceCandleCandleSplitNextOutExample {

    public static void main(String[] args) {

        BinanceSpotCandleOut binanceSpotCandleOut = new BinanceSpotCandleOut();


//        Config.setConfig("cryptocurrency.candle.dir.path","data/cryptocurrency/candle");
        BinanceCandle.csvNext(BinanceCandle.FUTURES_CANDLE, "BTCUSDT" , Times.MINUTE_1, YmdUtil.getTime("20220101"));
    }
}
