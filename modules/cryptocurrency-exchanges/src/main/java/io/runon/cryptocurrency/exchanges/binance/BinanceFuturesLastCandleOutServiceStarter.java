package io.runon.cryptocurrency.exchanges.binance;

import io.runon.cryptocurrency.trading.service.CandleOutRealTimeService;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

/**
 * @author macle
 */
public class BinanceFuturesLastCandleOutServiceStarter {

    public static void main(String[] args) {
        BinanceFuturesLastCandleStream stream = new BinanceFuturesLastCandleStream("binance_futures_last_candle");
        stream.connect();
        new DataStreamKeepAliveService().start();

        CandleOutRealTimeService candleOutRealTimeService = new CandleOutRealTimeService(stream);
        candleOutRealTimeService.start();
    }
}
