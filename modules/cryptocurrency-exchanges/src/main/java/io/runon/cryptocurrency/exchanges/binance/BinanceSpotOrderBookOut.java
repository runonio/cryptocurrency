package io.runon.cryptocurrency.exchanges.binance;

import io.runon.commons.config.Config;
import io.runon.cryptocurrency.trading.CryptocurrencyDataPath;
import lombok.extern.slf4j.Slf4j;

/**
 * 현물 호가창 내리기
 * 벡테스팅을 위한 데이터 저장용
 * @author macle
 */
@Slf4j
public class BinanceSpotOrderBookOut extends BinanceOrderBookOut {


    public BinanceSpotOrderBookOut(){
        outDirPath = CryptocurrencyDataPath.getSpotOrderBookDirPath();
        setSleepTime(Config.getLong("binance.spot.order.book.collect.sleep.time", 1000L));
        initUpdateMap();
    }

    @Override
    public String[] getAllSymbols() {
        return BinanceSpotApis.getAllSymbols();
    }



    @Override
    public String getJsonValue(String symbol) {
        return BinanceSpotApis.getOrderBook(symbol);
    }


}
