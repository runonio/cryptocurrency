package io.runon.cryptocurrency.exchanges.binance;

import com.binance.client.SyncRequestClient;
import com.binance.client.model.market.MarkPrice;
import com.seomse.crawling.core.http.HttpUrl;

import java.util.List;

/**
 * 바이낸스 티커정보
 * @author macle
 */
public class BinanceTickers {

    /**
     * example [{"symbol":"ETHBTC","price":"0.06529800"},{"symbol":"LTCBTC","price":"0.00287900"}]
     * @return json array
     */
    public String getTickers() {
        return HttpUrl.get("https://api.binance.com/api/v1/ticker/allPrices");
    }

    public List<MarkPrice> getFuturesTickers() {
        return getFuturesTickers("");
    }

    public List<MarkPrice> getFuturesTickers(String symbol) {
        return SyncRequestClient.create().getMarkPrice(symbol);
    }
}
