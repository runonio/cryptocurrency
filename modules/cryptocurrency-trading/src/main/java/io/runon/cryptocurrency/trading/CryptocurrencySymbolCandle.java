package io.runon.cryptocurrency.trading;

import io.runon.trading.data.csv.CsvSymbolCandle;
import io.runon.trading.technical.analysis.candle.IdCandles;

/**
 * @author macle
 */
public class CryptocurrencySymbolCandle {

    public static final String SPOT_PATH = CryptocurrencyDataPath.getSpotCandleDirPath();
    public static final String FUTURES_PATH = CryptocurrencyDataPath.getFuturesCandleDirPath();


    public static IdCandles[] getSpotSymbolCandles(String interval, long startOpenTime, long endTime ){
        return getSymbolCandles(SPOT_PATH, interval, startOpenTime, endTime);
    }

    public static IdCandles[] getFuturesSymbolCandles(String interval, long startOpenTime, long endTime ){
        return getSymbolCandles(FUTURES_PATH, interval, startOpenTime, endTime);
    }

    public static IdCandles[] getSymbolCandles(String path, String interval, long startOpenTime, long endTime){
        CsvSymbolCandle csvSymbolCandle = new CsvSymbolCandle(path, interval);
        return csvSymbolCandle.load(startOpenTime, endTime);
    }


}
