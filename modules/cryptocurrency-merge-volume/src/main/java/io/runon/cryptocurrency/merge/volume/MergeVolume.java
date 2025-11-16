package io.runon.cryptocurrency.merge.volume;

import io.runon.commons.utils.time.Times;
import io.runon.cryptocurrency.exchanges.binance.BinanceCandle;
import io.runon.cryptocurrency.trading.CandleVolumeMerge;
import io.runon.cryptocurrency.trading.CandleVolumeMergerStore;
import io.runon.cryptocurrency.trading.CryptocurrencyDataPath;
import io.runon.commons.math.BigDecimals;
import io.runon.trading.Trade;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.csv.CsvTimeFile;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import io.runon.trading.technical.analysis.candle.TradeCandles;
import io.runon.trading.technical.analysis.volume.Volumes;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 1분봉의 오차를 줄이기위해 데이터 저장 통계를 10초단위 생성
 * @author macle
 */
public class MergeVolume {


    //10초
    private final TradeCandles secondCandles;
    private final Object secondLock = new Object();
    private final Object volumeDataLock = new Object();


    @SuppressWarnings("FieldCanBeLocal")
    private final long secondTime = 10000L;
    @SuppressWarnings("FieldCanBeLocal")
    private final long minuteTime = Times.MINUTE_1;

    BigDecimal price;
    BigDecimal priceFutures;

    BigDecimal avg1m;
    BigDecimal avg5s;

    public MergeVolume(){
        secondCandles = new TradeCandles(secondTime);
        //30분까지 10초 단위면 180개 ( 보조로 200개까지 저장)
        secondCandles.setCount(200);
        secondCandles.setTradeRecord(false);
    }


    public void load(){
        //초기 데이터 세팅
        //초기 데이터는 1분봉을 나눠서 사용한다.
        //바이낸스 api를 통해 1분봉을 불러와서 사용

        int candleSaveCount = secondCandles.getCount();

        long time = System.currentTimeMillis();


        int limit = candleSaveCount/6 + 10;

        TradeCandle [] candles = BinanceCandle.candles(BinanceCandle.CANDLE, "BTCBUSD", "1m", null, TradingTimes.getOpenTime(Times.MINUTE_1, time) , limit);

        try {
            Thread.sleep(2000L);
        }catch(Exception ignore){}

        List<TradeCandle [] > addCandlesList = new ArrayList<>();

        addCandlesList.add(BinanceCandle.candles(BinanceCandle.CANDLE, "BTCUSDT", "1m", null, TradingTimes.getOpenTime(Times.MINUTE_1, time) , limit));

        addCandlesList.add(BinanceCandle.candles(BinanceCandle.FUTURES_CANDLE, "BTCUSDT", "1m", null, TradingTimes.getOpenTime(Times.MINUTE_1, time) , limit));
        try {
            Thread.sleep(2000L);
        }catch(Exception ignore){}
        addCandlesList.add(BinanceCandle.candles(BinanceCandle.FUTURES_CANDLE, "BTCBUSD", "1m", null, TradingTimes.getOpenTime(Times.MINUTE_1, time) , limit));
        CandleVolumeMerge.merge(candles, addCandlesList, Times.MINUTE_1);


        for (int i = 0; i < candles.length; i++) {
            long timeGap = (candles.length - i) * Times.MINUTE_1 ;
            TradeCandle candle = candles[i];

            BigDecimal buyVolume = candle.getBuyVolume().divide(BigDecimals.DECIMAL_6, MathContext.DECIMAL128);
            BigDecimal sellVolume = candle.getSellVolume().divide(BigDecimals.DECIMAL_6, MathContext.DECIMAL128);

            for (int j = 0; j < 6; j++) {
                secondCandles.addTrade(new Trade(Trade.Type.BUY, candle.getClose(), buyVolume, time - timeGap + (j * 10000L)));
                secondCandles.addTrade(new Trade(Trade.Type.SELL, candle.getClose(), sellVolume, time - timeGap + (j * 10000L)));
            }
        }

        avg();
    }


    private long avgLastOpenTime = -1;
    public void avg(){
        String spotCandleDirPath = CryptocurrencyDataPath.getSpotCandleDirPath();

        long lastOpenTime = CsvTimeFile.getLastTime( spotCandleDirPath +"/BTCBUSD/1h");
        if(avgLastOpenTime == lastOpenTime){
            return;
        }

        avgLastOpenTime = lastOpenTime;

        CandleVolumeMergerStore candleVolumeMergerStore = BinanceVolumeMerge.newCandleVolumeMerge("1h", Times.DAY_1*365);

        BigDecimal avg = Volumes.getAverage(candleVolumeMergerStore.newCandles(lastOpenTime), BigDecimals.DECIMAL_0_1 );
        avg1m = avg.divide(new BigDecimal(60), MathContext.DECIMAL128);
        avg5s = avg1m.divide(new BigDecimal(12), MathContext.DECIMAL128);

    }

    private long [] secondTimes;

    public long[] getSecondTimes() {
        return secondTimes;
    }

    public void setSecondTimes(long [] secondTimes) {
        this.secondTimes = secondTimes;
    }

    VolumePriceData volumeData = null;

    public void addTrade(Trade trade){
        synchronized (secondLock){
            secondCandles.addTrade(trade);
        }


        if(volumeData != null) {
            synchronized (volumeDataLock) {
                volumeData.addTrade(trade);
            }
        }
    }

    public void addTrade(Trade trade, int tradingCount){
        synchronized (secondLock){
            secondCandles.addTrade(trade).addTradingCount(tradingCount);
        }

        if(volumeData != null) {
            synchronized (volumeDataLock) {
                volumeData.addTrade(trade);
            }
        }
    }

    public VolumePriceData getVolumeData(){
        synchronized (volumeDataLock) {
            VolumePriceData last = this.volumeData;
            this.volumeData = new VolumePriceData();
            return last;
        }
    }

    public void addTrade(Trade [] trades){
        synchronized (secondLock){
            secondCandles.addTrade(trades);
        }

        if(volumeData != null) {
            synchronized (volumeDataLock) {
                for (Trade trade : trades) {
                    volumeData.addTrade(trade);
                }
            }
        }
    }

    public void addTrade(Trade [] trades, int tradingCount){
        synchronized (secondLock){
            secondCandles.addTrade(trades).addTradingCount(tradingCount);
        }

        if(volumeData != null) {
            synchronized (volumeDataLock) {
                for (Trade trade : trades) {
                    volumeData.addTrade(trade);
                }
            }
        }
    }

    public TradeCandle getSecondLastCandle(long time){
        long closeTime = System.currentTimeMillis();
        long openTime = closeTime - time;
        TradeCandle [] candles = secondCandles.getCandles();
        return TradeCandle.sumCandles(candles, openTime, closeTime);
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAvg1m() {
        return avg1m;
    }

    public BigDecimal getAvg5s() {
        return avg5s;
    }
}