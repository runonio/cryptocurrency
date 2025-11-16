package example.indicators;

import io.runon.commons.utils.time.YmdUtils;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TimeNumber;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.indicators.volume.Adi;
import io.runon.trading.technical.analysis.indicators.volume.ChaikinOscillator;
import io.runon.trading.view.TradingChart;

import java.time.ZoneId;

/**
 * @author macle
 */
public class BtcChaikinOscillator {
    public static void main(String[] args) {
        String symbol = "BTCUSDT";
        String interval = "1d";

        String path = CryptocurrencySymbolCandle.FUTURES_PATH + "/" + symbol + "/" + interval;
        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;
        long candleTime = TradingTimes.getIntervalTime(interval);

//        YmdUtils.getTime("20180101", zoneId), YmdUtils.getTime("20220922", zoneId
        long startTime = YmdUtils.getTime("20180101", zoneId);
        long endTime = YmdUtils.getTime("20220922", zoneId);

        TradeCandle[] candles = CsvCandle.load(path, candleTime, startTime, endTime);
        TimeNumber[] dataArray = Adi.getTimeNumbers(candles, 5000);

        TradingChart chart = new TradingChart(candles, 1700, 1000, TradingChart.ChartDateType.DAY);
        chart.addVolume(candles);
        chart.addLine(ChaikinOscillator.get(dataArray), "black", 1, false);

        chart.view();
    }
}
