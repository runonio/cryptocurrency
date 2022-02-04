package io.runon.cryptocurrency.exchanges.okx;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.seomse.crawling.core.http.HttpUrl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * okx 거래소 (과거이름 Okex)
 * https://www.okx.com/docs-v5/en/#market-maker-program
 * @author macle
 */
public class OkxExchange {

    public static String getTickers(){
        return getTickers("SPOT");
    }

    /**
     * Instrument type
     * SPOT
     * SWAP
     * FUTURES
     * OPTION
     * @param type SPOT or SWAP or FUTURES or OPTION
     * @return tickers json
     */
    public static String getTickers(String type){
        return HttpUrl.get("https://www.okx.com/api/v5/market/tickers?instType=" + type);
    }

    /**
     * ids
     * @param symbol BTC or ETH ...
     * @param option SPOT or SWAP or FUTURES or OPTION
     * @return ids
     */
    public static String [] getIds(String symbol, String option){
        String value = getTickers(option);
        JSONObject obj = new JSONObject(value);
        JSONArray array = obj.getJSONArray("data");

        List<String> idList = new ArrayList<>();

        for (int i = 0; i < array.length() ; i++) {
            obj = array.getJSONObject(i);
            String id = obj.getString("instId");
            if (id.startsWith(symbol + "-")){
                idList.add(id);
            }
        }

        return idList.toArray(new String[0]);
    }

    public static String getTradeSubscribeMessage(String ids){
        return getTradeSubscribeMessage(ids.split(","));
    }

    public static String getTradeSubscribeMessage(Collection<String> ids){
        return getTradeSubscribeMessage(ids.toArray(new String[0]));
    }

    public static String getTradeSubscribeMessage(String [] ids){
        Gson gson = new Gson();
        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("op", "subscribe");
        JsonArray args = new JsonArray();
        for(String id : ids){
            JsonObject idObj = new JsonObject();
            idObj.addProperty("channel", "trades");
            idObj.addProperty("instId", id);
            args.add(idObj);
        }
        messageObj.add("args", args);
        return gson.toJson(messageObj);
    }

    public static void main(String[] args) {

        String [] ids = getIds("BTC", "FUTURES");
        for(String id : ids){
            System.out.println(id);
        }
    }

}
