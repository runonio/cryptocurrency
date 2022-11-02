package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.commons.config.Config;
import lombok.extern.slf4j.Slf4j;

/**
 * 현물 호가창 내리기
 * 벡테스팅을 위한 데이터 저장용
 * @author macle
 */
@Slf4j
public class BinanceSpotOrderBookOut extends BinanceOrderBookOut {


    public BinanceSpotOrderBookOut(){
        outDirPath =  Config.getConfig("cryptocurrency.spot.order.book.dir.path","data/cryptocurrency/spot/order_book");
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
