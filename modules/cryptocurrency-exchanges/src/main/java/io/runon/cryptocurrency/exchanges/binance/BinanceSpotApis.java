package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.crawling.core.http.HttpUrl;

/**
 * 바이낸스 심볼과 통화 분리
 * github.com/binance/binance-spot-api-docs/blob/master/rest-api.md#data-sources
 * @author macle
 */
public class BinanceSpotApis {

    public static final String URL ="https://api.binance.com";

    public static String getOrderBook(String symbol){
        return HttpUrl.get(URL + "/api/v3/depth?symbol=" + symbol);
    }

}
