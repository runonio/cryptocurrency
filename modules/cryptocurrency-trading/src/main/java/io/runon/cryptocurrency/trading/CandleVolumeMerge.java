package io.runon.cryptocurrency.trading;

import com.seomse.commons.utils.time.Times;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.technical.analysis.candle.Candles;
import io.runon.trading.technical.analysis.candle.TradeCandle;

import java.io.File;
import java.time.ZoneId;
import java.util.List;

/**
 * 캔들 거래량 합치기
 * 암호화폐의 경우 여러 거래소, 여러 마켓에서 거래가 일어난다
 * 한 마켓이서의 거래량으로 수급을 분석하기 어려운경우 기준가격 마켓을 지정하고 다른마켓들의 거래량을 합쳐서 보는 분석을 사용할때 활용한다.
 * @author macle
 */
public class CandleVolumeMerge {


    //1d 4h, 5m 이런형식의 기준값
    private String interval = "1m";
    private long candleTime = Times.MINUTE_1;
    //기준경로
    private String path = CryptocurrencyDataPath.getSpotCandleDirPath() + "/BTCBUSD";

    // 추가 심볼 경로들
    private String [] addPaths = {
            CryptocurrencyDataPath.getSpotCandleDirPath() + "/BTCUSDT"
            , CryptocurrencyDataPath.getFuturesCandleDirPath() + "/BTCUSDT"
            , CryptocurrencyDataPath.getFuturesCandleDirPath() + "/BTCBUSD"
            };

    public void setInterval(String interval) {
        this.interval = interval;
        this.candleTime = TradingTimes.getIntervalTime(interval);
    }

    public void setCandleTime(long candleTime) {
        this.candleTime = candleTime;
        this.interval = TradingTimes.getInterval(candleTime);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAddPaths(String[] addPaths) {
        this.addPaths = addPaths;
    }


    public TradeCandle [] load(long startTime, long endTime){


        //기준 캔들 불러요기
        String absolutePath = new File(this.path).getAbsolutePath();

        TradeCandle [] candles = CsvCandle.load(absolutePath + "/" + interval, candleTime, startTime, endTime);

        // 거래량 추가하기
        for(String addPath : addPaths){

            String addAbsolutePath = new File(addPath).getAbsolutePath();

            if(absolutePath.equals(addAbsolutePath)){
                continue;
            }

            TradeCandle [] addCandles = CsvCandle.load(addAbsolutePath +"/" + interval, candleTime, startTime, endTime);
            for(TradeCandle addCandle : addCandles){
                int openTimeIndex = Candles.getOpenTimeIndex(candles, candleTime, addCandle.getOpenTime());
                if(openTimeIndex < 0){
                    continue;
                }

                TradeCandle candle = candles[openTimeIndex];
                candle.addVolume(addCandle);
            }
        }

        return candles;
    }


    public String getInterval() {
        return interval;
    }

    public long getCandleTime() {
        return candleTime;
    }

    public static void merge(TradeCandle [] candles, List<TradeCandle [] > addCandlesList, long candleTime) {
        for (TradeCandle [] addCandles : addCandlesList) {
            for(TradeCandle addCandle : addCandles){
                int openTimeIndex = Candles.getOpenTimeIndex(candles, candleTime, addCandle.getOpenTime());
                if(openTimeIndex < 0){
                    continue;
                }

                TradeCandle candle = candles[openTimeIndex];
                candle.addVolume(addCandle);
            }
        }
    }
}
