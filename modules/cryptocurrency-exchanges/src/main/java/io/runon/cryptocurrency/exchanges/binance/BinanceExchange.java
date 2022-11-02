package io.runon.cryptocurrency.exchanges.binance;

import com.binance.client.SyncRequestClient;
import com.binance.client.model.market.MarkPrice;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.seomse.crawling.core.http.HttpUrl;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.cryptocurrency.trading.exception.IdNotPatternException;
import io.runon.trading.BigDecimals;
import io.runon.trading.symbol.SymbolNumber;
import io.runon.trading.symbol.SymbolPriceVolume;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

/**
 * 바이낸스 심볼과 통화 분리
 * github.com/binance/binance-spot-api-docs/blob/master/rest-api.md#data-sources
 * @author macle
 */
public class BinanceExchange {

    public static MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        MarketSymbol marketSymbol = new MarketSymbol();
        marketSymbol.setId(cryptocurrencyId);

        cryptocurrencyId = cryptocurrencyId.toUpperCase();

        if(cryptocurrencyId.endsWith("USDT")){
            marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-4));
            marketSymbol.setMarket("USDT");
        }else if(cryptocurrencyId.endsWith("BUSD")){
            marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-4));
            marketSymbol.setMarket("BUSD");
        }else if(cryptocurrencyId.endsWith("BTC")){
            marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-3));
            marketSymbol.setMarket("BTC");
        }else if(cryptocurrencyId.endsWith("ETH")){
            marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-3));
            marketSymbol.setMarket("ETH");
        }else if(cryptocurrencyId.endsWith("BNB")){
            marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-3));
            marketSymbol.setMarket("BNB");
        } else{
            throw new IdNotPatternException("id: " + cryptocurrencyId);
        }

        return marketSymbol;
    }

    /**
     * example [{"symbol":"ETHBTC","price":"0.06529800"},{"symbol":"LTCBTC","price":"0.00287900"}]
     * @return json array
     */
    public static String getTickers() {
        return HttpUrl.get(BinanceSpotApis.URL + "/api/v3/ticker/price");
    }

    /**
     * @return json array
     */
    public static String getTickers24h() {
        return HttpUrl.get(BinanceSpotApis.URL + "/api/v3/ticker/24hr");
    }

    /**
     * USDT, BUSD 시장을 기준으로 거래대금 순서로 랭킹을 산출하여 돌려준다.
     * @return 랭킹맵
     */
    public static Map<String, Integer> getSymbolRankingMap(){

        Map<String, SymbolPriceVolume> symbolNumberMap = new HashMap<>();

        String jsonValue = BinanceExchange.getTickers24h();
        JSONArray array = new JSONArray(jsonValue);

        for (int i = 0 ; i < array.length() ; i++) {

            JSONObject object = array.getJSONObject(i);

            String symbol = object.getString("symbol");
            if(symbol.length() < 4){
                continue;
            }

            if(!symbol.endsWith("BUSD") &&  !symbol.endsWith("USDT")){
               continue;

            }

            symbol = symbol.substring(0,symbol.length()-4);
            SymbolPriceVolume symbolPriceVolume = symbolNumberMap.get(symbol);
            if(symbolPriceVolume == null){
                symbolPriceVolume = new SymbolPriceVolume();
                symbolPriceVolume.setSymbol(symbol);
                //평균가격
                symbolPriceVolume.setPrice(object.getBigDecimal("lastPrice").add(object.getBigDecimal("highPrice").add(object.getBigDecimal("lowPrice"))).divide(BigDecimals.DECIMAL_3, MathContext.DECIMAL128));
                symbolPriceVolume.setVolume(object.getBigDecimal("volume"));
                symbolNumberMap.put(symbol, symbolPriceVolume);
            }else{
                if(symbol.endsWith("BUSD")){
                    //BUSD 가격정보가 더 신뢰성이 높다고 판단
                    symbolPriceVolume.setPrice(object.getBigDecimal("lastPrice").add(object.getBigDecimal("highPrice").add(object.getBigDecimal("lowPrice"))).divide(BigDecimals.DECIMAL_3, MathContext.DECIMAL128));
                }
                symbolPriceVolume.setVolume(symbolPriceVolume.getVolume().add(object.getBigDecimal("volume")));
            }
        }


        Collection<SymbolPriceVolume> values = symbolNumberMap.values();

        SymbolNumber[] symbolNumbers = new SymbolNumber[values.size()];
        int index = 0;
        for(SymbolPriceVolume symbolPriceVolume : values){
            SymbolNumber symbolNumber = new SymbolNumber();
            symbolNumber.setSymbol(symbolPriceVolume.getSymbol());
            symbolNumber.setNumber(symbolPriceVolume.getPrice().multiply(symbolPriceVolume.getVolume()));
            symbolNumbers[index++] = symbolNumber;
        }

        Arrays.sort(symbolNumbers, SymbolNumber.SORT_NUMBER_DESC_SYMBOL_ASC );

        Map<String, Integer> rankingMap = new HashMap<>();
        int ranking = 1;
        BigDecimal lastNumber = symbolNumbers[0].getNumber();

        for(SymbolNumber symbolNumber : symbolNumbers){
            if(symbolNumber.getNumber().compareTo(lastNumber) != 0){
                ranking++;
                lastNumber = symbolNumber.getNumber();
            }
            rankingMap.put(symbolNumber.getSymbol(), ranking);
        }
        return rankingMap;
    }


    public static List<MarkPrice> getFuturesTickers() {
        return getFuturesTickers("");
    }

    public static List<MarkPrice> getFuturesTickers(String symbol) {
        return SyncRequestClient.create().getMarkPrice(symbol);
    }

    public static String getSymbolMarket(String symbol, String market){
        if(!symbol.endsWith(market)){
            symbol = symbol.toUpperCase();
            if(!symbol.endsWith(market)){
                symbol = symbol + market;
            }
        }
        return symbol;
    }

    //asks 매도호가 (매도가격)
    //bids 매수호가 (매수가격)


    /**
     * 표준 저장용 json 으로 변경
     * @param binanceOrderBookJson 바이낸스 order book (현물 선물 공통)
     * @return 표준 저장용 json
     */
    public static String getOrderBookLine(String binanceOrderBookJson){
        return getOrderBookLine(new JSONObject(binanceOrderBookJson));
    }

    public static String getOrderBookLine(JSONObject binanceOrderBookJson ){
        long time = System.currentTimeMillis();

        JSONArray askArray = binanceOrderBookJson.getJSONArray("asks");

        JsonArray asks = new JsonArray();
        for (int i = 0; i < askArray.length() ; i++) {
            //가격과 수량
            JSONArray pq = askArray.getJSONArray(i);

            JsonArray ask = new JsonArray();
            ask.add(pq.getBigDecimal(0).stripTrailingZeros().toPlainString());
            ask.add(pq.getBigDecimal(1).stripTrailingZeros().toPlainString());
            asks.add(ask);
        }

        JSONArray bidArray = binanceOrderBookJson.getJSONArray("bids");
        JsonArray bids = new JsonArray();
        for (int i = 0; i < bidArray.length() ; i++) {
            //가격과 수량
            JSONArray pq = bidArray.getJSONArray(i);

            JsonArray bid = new JsonArray();
            bid.add(pq.getBigDecimal(0).stripTrailingZeros().toPlainString());
            bid.add(pq.getBigDecimal(1).stripTrailingZeros().toPlainString());
            bids.add(bid);
        }


        JsonObject outObj = new JsonObject();
        outObj.addProperty("t", time);
        outObj.add("asks", asks);
        outObj.add("bids", bids);
        outObj.addProperty("update_id", binanceOrderBookJson.getLong("lastUpdateId"));

        return new Gson().toJson(outObj);
    }

    public static void main(String[] args) {
        System.out.println(getOrderBookLine(BinanceFuturesApis.getOrderBook("BTCUSDT")));
    }

}
