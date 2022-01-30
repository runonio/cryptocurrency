package io.runon.cryptocurrency.exchanges.binance;

import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.cryptocurrency.trading.exception.IdNotPatternException;

/**
 * 바이낸스 심볼과 통화 분리
 * @author macle
 */
public class BinanceMarketSymbol {

    public static MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        MarketSymbol marketSymbol = new MarketSymbol();
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
}
