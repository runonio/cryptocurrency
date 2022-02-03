package io.runon.cryptocurrency.exchanges.upbit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.seomse.crawling.core.http.HttpUrl;

/**
 * 업비트 거래소
 * https://docs.upbit.com/docs/upbit-quotation-websocket
 */
public class UpbitExchange {

    public static String getTickers(){
        return HttpUrl.get("https://api.upbit.com/v1/market/all");
    }

    public static String getSubscribeMessage(String symbols, String markets){
        Gson gson = new Gson();

        String [] symbolArray = symbols.toUpperCase().split(",");
        String [] marketArray = markets.toUpperCase().split(",");

        JsonArray message = new JsonArray();
        JsonObject ticket = new JsonObject();
        ticket.addProperty("ticket", "price");
        message.add(ticket);

        JsonObject ticker = new JsonObject();
        ticker.addProperty("type", "ticker");

        JsonArray ids = new JsonArray();
        for(String symbol : symbolArray){
            for(String market: marketArray){
                ids.add(market + "-" + symbol);
            }
        }

        ticker.add("codes",ids);
        message.add(ticker);

        return gson.toJson(message);
    }

    public static void main(String[] args) {
        System.out.println(HttpUrl.get("https://api.upbit.com/v1/market/all"));
    }
}
