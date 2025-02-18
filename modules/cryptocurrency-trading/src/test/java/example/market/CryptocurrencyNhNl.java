package example.market;

import io.runon.commons.utils.time.Times;
import io.runon.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TimeNumber;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvSymbolCandle;
import io.runon.trading.technical.analysis.candle.IdCandleTimes;
import io.runon.trading.technical.analysis.candle.IdCandles;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.candle.TradeCandles;
import io.runon.trading.technical.analysis.indicators.ma.Ema;
import io.runon.trading.technical.analysis.indicators.market.nhnl.Nhnl;
import io.runon.trading.technical.analysis.indicators.market.nhnl.NhnlData;
import io.runon.trading.view.TradingChart;

import java.time.ZoneId;
/**
 * nhnl 값 뽑아보기
 * @author macle
 */
public class CryptocurrencyNhNl {

    public static void main(String[] args) {

        String interval = "1d";
        long candleTime = TradingTimes.getIntervalTime(interval);


        String path = CryptocurrencySymbolCandle.SPOT_PATH;

        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;

        CsvSymbolCandle csvSymbolCandle = new CsvSymbolCandle(path,interval);

        String [] endWiths = {"USDT"};

        IdCandles[] symbolCandles = csvSymbolCandle.load(YmdUtil.getTime("20180101",zoneId),  System.currentTimeMillis()
                , null, endWiths
        );


        IdCandleTimes symbolCandleTimes = new IdCandleTimes(symbolCandles);

        Nhnl nhnl = new Nhnl(symbolCandleTimes);
        nhnl.setCandleTime(candleTime);
        nhnl.setTimeRange(Times.DAY_100);


        int n = 14;

        NhnlData[] array = nhnl.getArray(1500);

        System.out.println("ema ");

        TimeNumber [] nhArray = new TimeNumber[array.length];


        TimeNumber [] emaArray = Ema.getTimeNumbers(array, n, array.length-n);

        System.out.println(symbolCandles.length);
        System.out.println(array.length);

        TradeCandle[] candles = IdCandles.getCandles("BTCUSDT", symbolCandles);
        candles = TradeCandles.getCandles(candles, candles.length-1, array.length);


        TradingChart chart  = new TradingChart(candles, 1700, 1000,  TradingChart.ChartDateType.MINUTE);
        chart.addVolume(candles);
        chart.addLine(emaArray, "black", 1, false);
        chart.view();

        System.out.println(symbolCandleTimes.getTimes().length);

    }
}
