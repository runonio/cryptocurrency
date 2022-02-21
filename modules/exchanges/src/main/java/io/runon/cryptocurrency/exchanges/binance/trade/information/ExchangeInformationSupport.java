package io.runon.cryptocurrency.exchanges.binance.trade.information;

import com.binance.client.model.market.ExchangeInfoEntry;
import com.binance.client.model.market.MarkPrice;
import io.runon.cryptocurrency.exchanges.binance.trade.api.BinanceApiManager;
import io.runon.cryptocurrency.exchanges.binance.trade.exception.BinanceFuturesOrderLowerException;
import io.runon.cryptocurrency.exchanges.binance.trade.exception.BinanceFuturesSymbolNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeInformationSupport {

    private static class SingleTonHolder{ private static final ExchangeInformationSupport INSTANCE = new ExchangeInformationSupport();}
    com.binance.client.model.market.ExchangeInformation exchangeInformation;
    Map<String, ExchangeInfoEntry> symbolMap;

    private ExchangeInformationSupport(){
        this.exchangeInformation = BinanceApiManager.getInstance().getApi().getExchangeInformation();
        symbolMapSetting(exchangeInformation);
    }
    public static ExchangeInformationSupport getInstance(){return SingleTonHolder.INSTANCE;}

    private void symbolMapSetting(com.binance.client.model.market.ExchangeInformation information) {
        symbolMap = new HashMap<>();
        List<ExchangeInfoEntry> symbols = information.getSymbols();
        for (ExchangeInfoEntry symbol : symbols) {
            symbolMap.put(symbol.getSymbol(), symbol);
        }
    }

    public ExchangeInfoEntry getSymbol(String symbol){
        return symbolMap.get(symbol);
    }

    /**
     * 달러 기준의 주문 수량을 계산
     * @param symbol 심볼
     * @param dollar 달러
     * @return 수량
     * @throws BinanceFuturesSymbolNotFoundException 심볼을 못찾은 경우
     * @throws BinanceFuturesOrderLowerException 주문 가격이 낮은 경우
     */
    public BigDecimal getQuantityPrice(String symbol, BigDecimal dollar) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {
        ExchangeInfoEntry exchangeInfoEntry = symbolMap.get(symbol);
        if(exchangeInfoEntry == null){
            throw new BinanceFuturesSymbolNotFoundException(symbol);
        }
        Long quantityPrecision = exchangeInfoEntry.getQuantityPrecision();
        BigDecimal currentPrice = getMarketPrice(symbol);

        BigDecimal leverage = AccountInformationSupport.getInstance().getLeverage(symbol);
        BigDecimal quantity = dollar.divide(currentPrice, quantityPrecision.intValue(), RoundingMode.DOWN);

        if(!(quantity.compareTo(BigDecimal.ZERO) > 0)){

            BigDecimal dividedNum = new BigDecimal(1);
            for (int i = 0; i < quantityPrecision; i++) {
                dividedNum = dividedNum.multiply(new BigDecimal(10));
            }

            String message = "Binance Future [" + symbol + "] price '" + dollar + "', leverage '" + leverage + "' is lower. min dollar is ["
                    + currentPrice.divide(dividedNum, 2, RoundingMode.UP).toString() +
                    "]";
            throw new BinanceFuturesOrderLowerException(message);
        }

        return quantity;
    }

    /**
     * 마켓의 현재 가격을 가져온다.
     * @param symbol 심볼
     * @return 가격
     */
    private BigDecimal getMarketPrice(String symbol){
        List<MarkPrice> markPrices = BinanceApiManager.getInstance().getApi().getMarkPrice(symbol);
        return markPrices.get(0).getMarkPrice();
    }

}
