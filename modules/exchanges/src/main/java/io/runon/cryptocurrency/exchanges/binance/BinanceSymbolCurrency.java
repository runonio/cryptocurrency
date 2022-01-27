package io.runon.cryptocurrency.exchanges.binance;

import io.runon.cryptocurrency.trading.BaseCurrency;
import io.runon.cryptocurrency.trading.SymbolCurrency;
import io.runon.cryptocurrency.trading.exception.IdNotPatternException;

/**
 * 바이낸스 심볼과 통화 분리
 * @author macle
 */
public class BinanceSymbolCurrency {

    public static SymbolCurrency getSymbolCurrency(String cryptocurrencyId) {
        SymbolCurrency symbolCurrency = new SymbolCurrency();
        cryptocurrencyId = cryptocurrencyId.toUpperCase();

        if(cryptocurrencyId.endsWith("USDT")){
            symbolCurrency.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-4));
            symbolCurrency.setCurrency(BaseCurrency.USDT);
        }else if(cryptocurrencyId.endsWith("BUSD")){
            symbolCurrency.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-4));
            symbolCurrency.setCurrency(BaseCurrency.BUSD);
        }else{
            throw new IdNotPatternException("id: " + cryptocurrencyId);
        }

        return symbolCurrency;
    }
}
