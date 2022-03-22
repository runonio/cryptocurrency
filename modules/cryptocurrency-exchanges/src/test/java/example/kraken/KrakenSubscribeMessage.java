package example.kraken;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Kraken Subscribe message 예제
 * @author macle
 */
public class KrakenSubscribeMessage {
    public static void main(String[] args) {
        JsonObject sendMessageObj = new JsonObject();
        sendMessageObj.addProperty("event", "subscribe");

        JsonArray pair = new JsonArray();
        pair.add("XBT/USD");
        JsonObject subscription = new JsonObject();
        subscription.addProperty("name", "trade");
        sendMessageObj.add("subscription", subscription);
        sendMessageObj.add("pair", pair);

        System.out.println(new Gson().toJson(sendMessageObj));
    }
}
