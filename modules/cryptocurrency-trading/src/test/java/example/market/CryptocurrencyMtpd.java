package example.market;

import io.runon.commons.utils.time.YmdUtils;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TimeNumber;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvSymbolCandle;
import io.runon.trading.technical.analysis.candle.IdCandleTimes;
import io.runon.trading.technical.analysis.candle.IdCandles;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.candle.TradeCandles;
import io.runon.trading.technical.analysis.indicators.ma.Ema;
import io.runon.trading.technical.analysis.indicators.market.mv.Mtpd;
import io.runon.trading.view.TradingChart;

import java.math.BigDecimal;
import java.time.ZoneId;

/**
 * @author macle
 */
public class CryptocurrencyMtpd {
    public static void main(String[] args) {
        String path = CryptocurrencySymbolCandle.SPOT_PATH;

        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;

        CsvSymbolCandle csvSymbolCandle = new CsvSymbolCandle(path, "1d");

        String[] endWiths = {"USDT","BUSD"};

        IdCandles[] symbolCandles = csvSymbolCandle.load(YmdUtils.getTime("20180101", zoneId),  System.currentTimeMillis()
                , null, endWiths
        );

        IdCandleTimes symbolCandleTimes = new IdCandleTimes(symbolCandles);

        Mtpd mtpd = new Mtpd(symbolCandleTimes);
        mtpd.setAverageCount(500);
        mtpd.setMinAmount(new BigDecimal(10000));

        int n = 14;

        TimeNumber[] array = mtpd.getArray(10000);


        TimeNumber[] emaArray = Ema.getTimeNumbers(array, n, array.length - n);


        System.out.println(symbolCandles.length);
        System.out.println(array.length);

        TradeCandle[] candles = IdCandles.getCandles("BTCUSDT", symbolCandles);
        candles = TradeCandles.getCandles(candles, candles.length - 1, array.length);


        TradingChart chart = new TradingChart(candles, 1700, 1000, TradingChart.ChartDateType.DAY);
        chart.addVolume(candles);
        chart.addLine(emaArray, "black", 1, false);
        chart.view();

        System.out.println(symbolCandleTimes.getTimes().length);

    }
}
