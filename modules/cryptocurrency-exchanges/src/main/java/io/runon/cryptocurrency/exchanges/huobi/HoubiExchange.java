package io.runon.cryptocurrency.exchanges.huobi;

import com.huobi.client.MarketClient;
import com.huobi.constant.HuobiOptions;
import com.huobi.model.market.MarketTicker;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.cryptocurrency.trading.exception.IdNotPatternException;

import java.util.ArrayList;
import java.util.List;

/**
 * 후오비 거래소 공통
 * @author macle
 */
public class HoubiExchange {

    public static MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        MarketSymbol marketSymbol = new MarketSymbol();
        marketSymbol.setId(cryptocurrencyId);
        cryptocurrencyId = cryptocurrencyId.toUpperCase();

        if(cryptocurrencyId.endsWith("USDT")){
            marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-4));
            marketSymbol.setMarket("USDT");
        }else if(cryptocurrencyId.endsWith("BTC")){
            marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-3));
            marketSymbol.setMarket("BTC");
        }else if(cryptocurrencyId.endsWith("ETH")){
            marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-3));
            marketSymbol.setMarket("ETH");
        }else{
            throw new IdNotPatternException("id: " + cryptocurrencyId);
        }

        return marketSymbol;
    }

    public MarketTicker[] getTickers(){
        List<MarketTicker> usdtList = new ArrayList<>();

        List<MarketTicker> marketTickerList = MarketClient.create(new HuobiOptions()).getTickers();
        for(MarketTicker marketTicker : marketTickerList) {
            String marketSymbol = marketTicker.getSymbol();
            if (!marketSymbol.endsWith("usdt") && !marketSymbol.endsWith("btc")) {
                continue;
            }
            usdtList.add(marketTicker);
        }

        return usdtList.toArray(new MarketTicker[0]);
    }
}
