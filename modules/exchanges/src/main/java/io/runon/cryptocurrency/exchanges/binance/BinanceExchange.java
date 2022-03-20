package io.runon.cryptocurrency.exchanges.binance;

import com.binance.client.SyncRequestClient;
import com.binance.client.model.market.MarkPrice;
import com.seomse.crawling.core.http.HttpUrl;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.cryptocurrency.trading.exception.IdNotPatternException;

import java.util.List;

/**
 * 바이낸스 심볼과 통화 분리
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
    public String getTickers() {
        return HttpUrl.get("https://api.binance.com/api/v1/ticker/allPrices");
    }

    public List<MarkPrice> getFuturesTickers() {
        return getFuturesTickers("");
    }

    public List<MarkPrice> getFuturesTickers(String symbol) {
        return SyncRequestClient.create().getMarkPrice(symbol);
    }

}
