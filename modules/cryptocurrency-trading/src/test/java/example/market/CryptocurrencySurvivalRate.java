package example.market;

import io.runon.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TimeNumber;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvSymbolCandle;
import io.runon.trading.technical.analysis.candle.IdCandleTimes;
import io.runon.trading.technical.analysis.candle.IdCandles;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.candle.TradeCandles;
import io.runon.trading.technical.analysis.indicators.market.MarketSurvivalRate;
import io.runon.trading.view.TradingChart;

import java.math.BigDecimal;
import java.time.ZoneId;

/**
 * @author macle
 */
public class CryptocurrencySurvivalRate {

    public static void main(String[] args) {
        String path = CryptocurrencySymbolCandle.SPOT_PATH;

        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;

        CsvSymbolCandle csvSymbolCandle = new CsvSymbolCandle(path, "1d");

        String[] endWiths = {"USDT","BUSD"};

        IdCandles[] symbolCandles = csvSymbolCandle.load(YmdUtil.getTime("20180101", zoneId),  System.currentTimeMillis()
                , null, endWiths
        );

        IdCandleTimes symbolCandleTimes = new IdCandleTimes(symbolCandles);

        MarketSurvivalRate msr = new MarketSurvivalRate(symbolCandleTimes);
        msr.setMinAmount(new BigDecimal(10000));

        int n = 14;

        TimeNumber[] array = msr.getArray(2000);

        TradeCandle[] candles = IdCandles.getCandles("BTCBUSD", symbolCandles);
        candles = TradeCandles.getCandles(candles, candles.length - 1, array.length);

        TradingChart chart = new TradingChart(candles, 1700, 1000, TradingChart.ChartDateType.DAY);
        chart.addVolume(candles);
        chart.addLine(array, "black", 1, false);
        chart.view();

    }

}
