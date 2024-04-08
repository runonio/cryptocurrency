package io.runon.cryptocurrency.exchanges.binance;

import com.seomse.commons.http.HttpApis;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 바이낸스 심볼과 통화 분리
 * github.com/binance/binance-spot-api-docs/blob/master/rest-api.md#data-sources
 * @author macle
 */
public class BinanceSpotApis {

    public static final String URL ="https://api.binance.com";

    public static String getOrderBook(String symbol){
        return HttpApis.getMessage(URL + "/api/v3/depth?symbol=" + symbol);
    }


    public static String [] getAllSymbols(){
        String jsonValue = BinanceExchange.getTickers();
        JSONArray jsonArray = new JSONArray(jsonValue);

        String [] allSymbols = new String[jsonArray.length()];

        int length = jsonArray.length();
        for (int i = 0; i < length ; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            allSymbols[i] = jsonObject.getString("symbol");
        }
        return allSymbols;
    }

}
