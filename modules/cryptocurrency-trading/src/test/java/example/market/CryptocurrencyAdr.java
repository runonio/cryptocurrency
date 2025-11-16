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
import io.runon.trading.technical.analysis.indicators.market.Adr;
import io.runon.trading.view.TradingChart;

import java.time.ZoneId;

/**
 * Adr 값 뽑아보기
 * @author macle
 */
public class CryptocurrencyAdr {

    public static void main(String[] args) {
        String path = CryptocurrencySymbolCandle.SPOT_PATH;

        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;

        CsvSymbolCandle csvSymbolCandle = new CsvSymbolCandle(path,"1d");

        String[] endWiths = {"USDT","BUSD"};

        IdCandles[] symbolCandles = csvSymbolCandle.load(YmdUtils.getTime("20180101",zoneId),  System.currentTimeMillis()
        , null, endWiths
        );


        IdCandleTimes symbolCandleTimes = new IdCandleTimes(symbolCandles);

        Adr adr = new Adr(symbolCandleTimes);

        int n = 14;

        TimeNumber [] array = adr.getArray(2000);
        TimeNumber [] emaArray = Ema.getTimeNumbers(array, n, array.length-n);

        TradeCandle [] candles = IdCandles.getCandles("BTCBUSD", symbolCandles);
        candles = TradeCandles.getCandles(candles, candles.length-1, array.length);


        TradingChart chart  = new TradingChart(candles, 1700, 1000,  TradingChart.ChartDateType.MINUTE);
        chart.addVolume(candles);
        chart.addLine(emaArray, "black", 1, false);
        chart.view();

    }

}
