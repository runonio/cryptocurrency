package example.kraken;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
/**
 * Kraken 선물
 * @author macle
 */
public class KrakenFuturesSubscribeMessage {
    public static void main(String[] args) {
        JsonObject sendMessageObj = new JsonObject();
        sendMessageObj.addProperty("event", "subscribe");

        JsonArray ids = new JsonArray();
        ids.add("PI_XBTUSD");

        sendMessageObj.addProperty("feed", "trade");
        sendMessageObj.add("product_ids", ids);

        System.out.println(new Gson().toJson(sendMessageObj));
    }
}
