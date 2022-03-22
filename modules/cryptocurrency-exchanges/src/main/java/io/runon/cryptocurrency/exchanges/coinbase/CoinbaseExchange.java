package io.runon.cryptocurrency.exchanges.coinbase;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.seomse.crawling.core.http.HttpUrl;

/**
 * 코인베이스 거래소
 * https://docs.cloud.coinbase.com/exchange/docs/overview
 * @author macle
 */
public class CoinbaseExchange {

    public static String getTickers(){
        return HttpUrl.get("https://api.exchange.coinbase.com/products");
    }

    public static String getSubscribeMessage(String symbols, String markets){
        Gson gson = new Gson();

        String [] symbolArray = symbols.toUpperCase().split(",");
        String [] marketArray = markets.toUpperCase().split(",");

        JsonObject message = new JsonObject();
        message.addProperty("type", "subscribe");

        JsonObject ticker = new JsonObject();
        ticker.addProperty("name", "ticker");

        JsonArray ids = new JsonArray();
        for(String symbol : symbolArray){
            for(String market: marketArray){
                ids.add(symbol + "-" + market);
            }
        }
        ticker.add("product_ids", ids);

        JsonArray channels = new JsonArray();
        channels.add(ticker);
        message.add("channels", channels);

        return gson.toJson(message);
    }

}
