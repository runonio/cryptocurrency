package example.indicators;

import io.runon.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.indicators.band.BollingerBands;
import io.runon.trading.technical.analysis.indicators.band.BollingerBandsData;
import io.runon.trading.technical.analysis.indicators.ma.Ema;
import io.runon.trading.view.TradingChart;

import java.math.BigDecimal;
import java.time.ZoneId;

/**
 * @author macle
 */
public class BtcBollingerBands {
    public static void main(String[] args) {

        String symbol = "BTCUSDT";
        String interval = "1d";

        String path = CryptocurrencySymbolCandle.FUTURES_PATH + "/" + symbol + "/" + interval;
        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;

        long candleTime = TradingTimes.getIntervalTime(interval);

//        YmdUtil.getTime("20180101", zoneId), YmdUtil.getTime("20220922", zoneId
        long startTime = YmdUtil.getTime("20180101", zoneId);
        long endTime = YmdUtil.getTime("20220922", zoneId);

        TradeCandle[] candles = CsvCandle.load(path, candleTime, startTime, endTime);
        //SMA
//        BollingerBandsData[] dataArray = BollingerBands.getArray(candles, 5000);

        //EMA
        BigDecimal [] emaArray  = Ema.getArray(candles, 20, candles.length);
        BollingerBandsData[] dataArray = BollingerBands.getArray(candles,emaArray, 20, new BigDecimal(2),5000);

        TradingChart chart = new TradingChart(candles, 1700, 1000, TradingChart.ChartDateType.DAY);
        chart.addVolume(candles);

        chart.addLine(BollingerBands.getMbbArray(dataArray), "black", 1);
        chart.addLine(BollingerBands.getUbbArray(dataArray), "red", 1);
        chart.addLine(BollingerBands.getLbbArray(dataArray), "blue", 1);
        chart.addLine(BollingerBands.getBwArray(dataArray), "black", 2, false);

        chart.view();
//
//        System.out.println(symbolCandleTimes.getTimes().length);



    }
}
