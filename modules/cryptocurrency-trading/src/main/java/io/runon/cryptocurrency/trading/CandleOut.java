package io.runon.cryptocurrency.trading;

import io.runon.trading.CandleTimes;

/**
 * @author macle
 */
public abstract class CandleOut extends SymbolsData {

    protected long [] candleTimes;


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


}
