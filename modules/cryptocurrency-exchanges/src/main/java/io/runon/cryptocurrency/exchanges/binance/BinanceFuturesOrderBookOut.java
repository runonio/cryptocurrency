package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.commons.config.Config;

/**
 * 선물 호가창 내리기
 * 벡테스팅을 위한 데이터 저장용
 * @author macle
 */
public class BinanceFuturesOrderBookOut extends BinanceOrderBookOut {


    public BinanceFuturesOrderBookOut(){
        outDirPath =  Config.getConfig("cryptocurrency.futures.order.book.dir.path","data/cryptocurrency/futures/order_book");
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
