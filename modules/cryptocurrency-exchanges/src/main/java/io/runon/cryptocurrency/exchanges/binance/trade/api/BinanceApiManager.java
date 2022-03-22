package io.runon.cryptocurrency.exchanges.binance.trade.api;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import io.runon.commons.data.service.DataServiceYml;

public class BinanceApiManager {
    private static class SingleTonHolder{ private static final BinanceApiManager INSTANCE = new BinanceApiManager();}

    SyncRequestClient syncRequestClient;
    private BinanceApiManager(){
        init();
    }

    private void init() {
        RequestOptions options = new RequestOptions();
        String apiKey = DataServiceYml.getYmlMap("binance").get("apikey").toString();
        String secret = DataServiceYml.getYmlMap("binance").get("secret").toString();
        syncRequestClient = SyncRequestClient.create(apiKey, secret, options);
    }

    public static BinanceApiManager getInstance(){return SingleTonHolder.INSTANCE;}

    public SyncRequestClient getApi(){
        return syncRequestClient;
    }
}
