package example.market;

import io.runon.commons.utils.time.YmdUtils;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TimeNumber;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvSymbolCandle;
import io.runon.trading.technical.analysis.candle.*;
import io.runon.trading.technical.analysis.candle.IdCandlesGet;
import io.runon.trading.technical.analysis.candle.TradeCandlesGet;
import io.runon.trading.technical.analysis.indicators.market.AdIssue;
import io.runon.trading.technical.analysis.indicators.market.McClellanOscillator;
import io.runon.trading.view.TradingChart;

import java.time.ZoneId;

/**
 * @author macle
 */
public class CryptocurrencyMcClellanOscillator {
    public static void main(String[] args) {
        String path = CryptocurrencySymbolCandle.FUTURES_PATH;

        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;

        CsvSymbolCandle csvSymbolCandle = new CsvSymbolCandle(path,"15m");

        String [] endWiths = {"USDT"};

        IdCandlesGet[] symbolCandles = csvSymbolCandle.load(YmdUtils.getTime("20220101",zoneId),  System.currentTimeMillis()
                , null, endWiths
        );


        IdCandleTimes symbolCandleTimes = new IdCandleTimes(symbolCandles);

        AdIssue adIssue = new AdIssue(symbolCandleTimes);

        int n = 20;

        TimeNumber[] array = adIssue.getArray(2000);

        TradeCandle[] candles = IdCandlesGet.getCandles("BTCUSDT", symbolCandles);
        candles = TradeCandlesGet.getCandles(candles, candles.length-1, array.length);


        TradingChart chart  = new TradingChart(candles, 1700, 1000,  TradingChart.ChartDateType.MINUTE);
        chart.addVolume(candles);
        chart.addLine(McClellanOscillator.get(array), "black", 1, false);
        chart.view();

    }
}
