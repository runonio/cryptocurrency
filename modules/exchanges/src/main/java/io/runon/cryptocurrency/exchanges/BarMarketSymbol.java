package io.runon.cryptocurrency.exchanges;

import io.runon.cryptocurrency.trading.MarketSymbol;
/**
 * BTC-USD 형태의 - 를 활용한 마켓구분
 * @author macle
 */
public class BarMarketSymbol {

    //왼쪽이 심볼인경우
    public static MarketSymbol leftSymbol(String cryptocurrencyId) {
        MarketSymbol marketSymbol = new MarketSymbol();
        cryptocurrencyId = cryptocurrencyId.toUpperCase();

        int index = cryptocurrencyId.indexOf("-");
        marketSymbol.setSymbol(cryptocurrencyId.substring(0, index));
        marketSymbol.setMarket(cryptocurrencyId.substring(index + 1));

        return marketSymbol;
    }
    
    //오른쪽이 심볼인경우
    public static MarketSymbol rightSymbol(String cryptocurrencyId) {
        MarketSymbol marketSymbol = new MarketSymbol();
        cryptocurrencyId = cryptocurrencyId.toUpperCase();

        int index = cryptocurrencyId.indexOf("-");
        marketSymbol.setMarket(cryptocurrencyId.substring(0, index));
        marketSymbol.setSymbol(cryptocurrencyId.substring(index + 1));

        return marketSymbol;
    }
}
