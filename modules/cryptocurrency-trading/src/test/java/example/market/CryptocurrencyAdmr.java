package example.market;

import com.seomse.commons.utils.time.Times;
import com.seomse.commons.utils.time.YmdUtil;
import io.runon.cryptocurrency.trading.CryptocurrencySymbolCandle;
import io.runon.trading.TimeNumber;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvSymbolCandle;
import io.runon.trading.technical.analysis.candle.IdCandleTimes;
import io.runon.trading.technical.analysis.candle.IdCandles;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.candle.TradeCandles;
import io.runon.trading.technical.analysis.indicators.ma.Ema;
import io.runon.trading.technical.analysis.indicators.market.Admr;
import io.runon.trading.view.TradingChart;

import java.time.ZoneId;

/**
 * Admr 값 뽑아보기
 * @author macle
 */
public class CryptocurrencyAdmr {


    public static void main(String[] args) {
        String path = CryptocurrencySymbolCandle.SPOT_PATH;

        ZoneId zoneId = TradingTimes.UTC_ZONE_ID;

        CsvSymbolCandle csvSymbolCandle = new CsvSymbolCandle(path,"1d");

        String[] endWiths = {"USDT","BUSD"};

        IdCandles[] symbolCandles = csvSymbolCandle.load(YmdUtil.getTime("20180101",zoneId),  System.currentTimeMillis()
                , null, endWiths
        );


        IdCandleTimes symbolCandleTimes = new IdCandleTimes(symbolCandles);

        Admr admr = new Admr(symbolCandleTimes);

        int n = 14;


        TimeNumber[] array = admr.getArray(500);


        System.out.println("ema ");

        TimeNumber [] emaArray = Ema.getTimeNumbers(array, n, array.length-n);
        for (TimeNumber timeNumber : emaArray){
            System.out.println(Times.ymdhm(timeNumber.getTime(), TradingTimes.KOR_ZONE_ID) +", " + timeNumber.getNumber().toPlainString());
        }

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
