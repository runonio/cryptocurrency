package example;

import io.runon.cryptocurrency.trading.CandleVolumeMerge;
import io.runon.trading.technical.analysis.candle.TradeCandle;

/**
 *
 * @author macle
 */
public class BtcCandleVolumeMerge {


    public static void main(String[] args) {

        CandleVolumeMerge candleVolumeMerge = new CandleVolumeMerge();
        //첫 캔들 open time, 마지막캔들 close time
        TradeCandle [] candles = candleVolumeMerge.load(1664596800000L, 1664598840000L);

        for(TradeCandle candle : candles){
            System.out.println(candle.toString());
        }

//        1664596800000

//        1664598840000
    }
}
