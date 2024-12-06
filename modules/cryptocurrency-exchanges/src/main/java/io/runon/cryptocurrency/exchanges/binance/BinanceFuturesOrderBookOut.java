package io.runon.cryptocurrency.exchanges.binance;

import io.runon.commons.config.Config;
import io.runon.cryptocurrency.trading.CryptocurrencyDataPath;

/**
 * 선물 호가창 내리기
 * 벡테스팅을 위한 데이터 저장용
 * @author macle
 */
public class BinanceFuturesOrderBookOut extends BinanceOrderBookOut {


    public BinanceFuturesOrderBookOut(){
        outDirPath = CryptocurrencyDataPath.getFuturesOrderBookDirPath();
        setSleepTime(Config.getLong("binance.futures.order.book.collect.sleep.time", 1000L));
        initUpdateMap();
    }

    @Override
    public String getJsonValue(String symbol) {
        return BinanceFuturesApis.getOrderBook(symbol);
    }

    @Override
    public String[] getAllSymbols() {
        return BinanceFuturesApis.getAllSymbols();
    }
}
