package example.indicators;

import com.seomse.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.indicators.sar.Sar;
import io.runon.trading.technical.analysis.indicators.sar.SarData;
import io.runon.trading.view.TradingChart;

import java.time.ZoneId;

/**
 * @author macle
 */
public class BtcSar {

    public static void main(String[] args) {
        String symbol = "BTCBUSD";
        String interval = "1d";

        String path = CryptocurrencySymbolCandle.SPOT_PATH + "/" + symbol + "/" + interval;
        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;
        long candleTime = TradingTimes.getIntervalTime(interval);

        long startTime = YmdUtil.getTime("20220101", zoneId);
        long endTime = System.currentTimeMillis();

        TradeCandle[] candles = CsvCandle.load(path, candleTime, startTime, endTime);

        Sar sar = new Sar();

        SarData [] dataArray = sar.getArray(candles, 5000);

        TradingChart chart = new TradingChart(candles, 1700, 1000, TradingChart.ChartDateType.DAY);
        chart.addVolume(candles);

        chart.addLine(Sar.getAdvancingArray(dataArray), "blue", 1);
        chart.addLine(Sar.getDeclineArray(dataArray), "red", 1);

        chart.view();
    }
}
