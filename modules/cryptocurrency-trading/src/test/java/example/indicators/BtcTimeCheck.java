package example.indicators;

import io.runon.commons.utils.time.Times;
import io.runon.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.technical.analysis.candle.TradeCandle;

import java.time.ZoneId;

/**
 * @author macle
 */
public class BtcTimeCheck {
    public static void main(String[] args) {
        String symbol = "BTCBUSD";
        String interval = "12h";

        String path = CryptocurrencySymbolCandle.SPOT_PATH + "/BTCBUSD/" + interval;
        long candleTime = TradingTimes.getIntervalTime(interval);

        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;

        long startTime = YmdUtil.getTime("20180101", zoneId);
        TradeCandle[] candles = CsvCandle.load(path, candleTime, startTime, System.currentTimeMillis());

        for(TradeCandle tradeCandle : candles){
            System.out.println( (tradeCandle.getOpenTime())==TradingTimes.getOpenTime(Times.HOUR_12, tradeCandle.getOpenTime(), TradingTimes.UTC_ZONE_ID));
        }

    }
}
