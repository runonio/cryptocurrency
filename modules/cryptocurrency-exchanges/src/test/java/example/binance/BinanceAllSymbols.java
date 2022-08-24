package example.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author macle
 */
public class BinanceAllSymbols {
    public static void main(String[] args) {
        String tickersJson = BinanceExchange.getTickers();
        JSONArray jsonArray = new JSONArray(tickersJson);

        for (int i = 0; i < jsonArray.length() ; i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            System.out.println(object.getString("symbol"));
        }
    }
}
