package example.indicators;

import io.runon.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.indicators.elder.ForceIndex;
import io.runon.trading.technical.analysis.indicators.elder.ForceIndexData;
import io.runon.trading.view.TradingChart;

import java.time.ZoneId;

/**
 * @author macle
 */
public class BtcForceIndex {
    public static void main(String[] args) {
        String symbol = "BTCUSDT";
        String interval = "1d";

        String path = CryptocurrencySymbolCandle.FUTURES_PATH + "/" + symbol + "/" + interval;
        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;
        long candleTime = TradingTimes.getIntervalTime(interval);

        long startTime = YmdUtil.getTime("20180101", zoneId);
        long endTime = YmdUtil.getTime("20220922", zoneId);

        ForceIndex forceIndex = new ForceIndex();
        TradeCandle[] candles = CsvCandle.load(path, candleTime, startTime, endTime);
        ForceIndexData[] dataArray = forceIndex.getArray(candles, 5000);

        TradingChart chart = new TradingChart(candles, 1700, 1000, TradingChart.ChartDateType.DAY);
        chart.addVolume(candles);

        chart.addLine(ForceIndex.getShortIndexes(dataArray), "blue", 1, false);
        chart.addLine(ForceIndex.getLongIndexes(dataArray), "red", 1, false);
        chart.view();

    }
}
