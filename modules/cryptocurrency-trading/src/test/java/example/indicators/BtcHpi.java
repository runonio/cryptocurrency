package example.indicators;

import io.runon.commons.utils.time.Times;
import io.runon.commons.utils.time.YmdUtil;
import io.runon.trading.BigDecimals;
import io.runon.trading.TimeNumber;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.data.csv.CsvOpenInterest;
import io.runon.trading.oi.OpenInterestSymbolStorage;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.indicators.volume.Hpi;
import io.runon.trading.view.TradingChart;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;

import java.time.ZoneId;

/**
 * @author macle
 */
public class BtcHpi {
    public static void main(String[] args) {

        String symbol = "BTCUSDT";
        String interval = "5m";


        String path = CryptocurrencySymbolCandle.FUTURES_PATH + "/" + symbol + "/" + interval;
        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;
        long candleTime = TradingTimes.getIntervalTime(interval);

//        YmdUtil.getTime("20180101", zoneId), YmdUtil.getTime("20220922", zoneId
        long startTime = YmdUtil.getTime("20220720", zoneId);
        long endTime = YmdUtil.getTime("20220929", zoneId);

        OpenInterestSymbolStorage usdtOpenInterestStorage = new OpenInterestSymbolStorage();
        usdtOpenInterestStorage.setDataTimeGap(Times.MINUTE_5);
        usdtOpenInterestStorage.add(CsvOpenInterest.loadOpenInterest("D:\\data\\cryptocurrency\\futures\\open_interest\\5m\\BTCUSDT", usdtOpenInterestStorage.getMaxLength()));
        OpenInterestSymbolStorage busdOpenInterestStorage = new OpenInterestSymbolStorage();
        busdOpenInterestStorage.setDataTimeGap(Times.MINUTE_5);
        busdOpenInterestStorage.add(CsvOpenInterest.loadOpenInterest("D:\\data\\cryptocurrency\\futures\\open_interest\\5m\\BTCBUSD",  busdOpenInterestStorage.getMaxLength()));


        Hpi hpi = new Hpi(usdtOpenInterestStorage);
        hpi.setS(BigDecimals.DECIMAL_20);
        hpi.setDenominator(BigDecimals.DECIMAL_100000);

        TradeCandle[] candles = CsvCandle.load(path, candleTime, startTime, endTime);
        TimeNumber[] dataArray = hpi.getArray(candles, 5000);

        TradingChart chart = new TradingChart(candles, 1700, 1000, TradingChart.ChartDateType.MINUTE);
        chart.addVolume(candles);
        chart.addLine(dataArray, "black", 1, false);
        chart.view();
    }
}
