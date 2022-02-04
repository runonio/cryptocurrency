package example.ftx;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class FtxSubscribeMessage {
    public static void main(String[] args) {
        JsonObject sendMessageObj = new JsonObject();
        sendMessageObj.addProperty("channel", "trades");
        sendMessageObj.addProperty("op", "subscribe");
        sendMessageObj.addProperty("market", "BTC-PERP");

        System.out.println(new Gson().toJson(sendMessageObj));
    }
}
