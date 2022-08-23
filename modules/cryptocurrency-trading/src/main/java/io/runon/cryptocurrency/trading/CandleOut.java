package io.runon.cryptocurrency.trading;

import io.runon.trading.CandleTimes;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * @author macle
 */
public abstract class CandleOut {

    protected String [] symbols;
    protected long [] candleTimes;

    protected String outDirPath;

    public void setSymbols(String[] symbols) {
        this.symbols = symbols;
    }

    public void setIntervals(String [] intervals) {
        long [] candleTimes = new long[intervals.length];
        for (int i = 0; i <intervals.length ; i++) {
            candleTimes[i] = CandleTimes.getIntervalTime(intervals[i]);
        }
        this.candleTimes = candleTimes;
    }

    public void setCandleTimes(long[] candleTimes) {
        this.candleTimes = candleTimes;
    }

    public void setOutDirPath(String outDirPath) {
        this.outDirPath = outDirPath;
    }

    protected ZoneId zoneId = CandleTimes.US_STOCK_ZONE_ID;

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    protected long startOpenTime = -1;

    public void setStartOpenTime(long startOpenTime) {
        this.startOpenTime = startOpenTime;
    }

    public void setSymbolsMarket(String market) {

        String [] allSymbols = getAllSymbols();
        List<String> symbolList = new ArrayList<>();

        for (String symbol : allSymbols) {
            if(symbol.endsWith(market)){
                String leftSymbol = symbol.substring(0, symbol.length() - market.length());
                if(Markets.isMarketIndexSymbol(symbol)){
                    symbolList.add(symbol);
                }
            }
        }
        symbols = symbolList.toArray(new String[0]);
    }

    public void setSymbolsMarket(String [] markets) {
        String [] allSymbols = getAllSymbols();
        List<String> symbolList = new ArrayList<>();

        for (String symbol : allSymbols) {
            for(String market : markets){
                if(symbol.endsWith(market)){
                    String leftSymbol = symbol.substring(0, symbol.length() - market.length());
                    if(Markets.isMarketIndexSymbol(symbol)){
                        symbolList.add(symbol);
                        break;
                    }
                }
            }
        }
        symbols = symbolList.toArray(new String[0]);
    }

    public abstract String [] getAllSymbols();

}
