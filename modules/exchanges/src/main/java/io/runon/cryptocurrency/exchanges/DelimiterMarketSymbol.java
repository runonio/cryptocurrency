package io.runon.cryptocurrency.exchanges;

import io.runon.cryptocurrency.trading.MarketSymbol;

/**
 * BTC-USD 형태의 구분자가 있는 마켓 심볼 구분
 * @author macle
 */
public class DelimiterMarketSymbol {

    //왼쪽이 심볼인경우
    public static MarketSymbol leftSymbol(String delimiter, String cryptocurrencyId) {
        MarketSymbol marketSymbol = new MarketSymbol();
        cryptocurrencyId = cryptocurrencyId.toUpperCase();

        int index = cryptocurrencyId.indexOf(delimiter);
        marketSymbol.setSymbol(cryptocurrencyId.substring(0, index));
        marketSymbol.setMarket(cryptocurrencyId.substring(index + 1));

        return marketSymbol;
    }
    
    //오른쪽이 심볼인경우
    public static MarketSymbol rightSymbol(String delimiter, String cryptocurrencyId) {
        MarketSymbol marketSymbol = new MarketSymbol();
        cryptocurrencyId = cryptocurrencyId.toUpperCase();

        int index = cryptocurrencyId.indexOf(delimiter);
        marketSymbol.setMarket(cryptocurrencyId.substring(0, index));
        marketSymbol.setSymbol(cryptocurrencyId.substring(index + 1));

        return marketSymbol;
    }

    public static MarketSymbol getMarketSymbol(String delimiter, String cryptocurrencyId, int marketIndex, int symbolIndex){
        MarketSymbol marketSymbol = new MarketSymbol();
        String [] values = cryptocurrencyId.split(delimiter);
        marketSymbol.setMarket(values[marketIndex].toUpperCase());
        marketSymbol.setSymbol(values[symbolIndex].toUpperCase());
        return marketSymbol;
    }
}
