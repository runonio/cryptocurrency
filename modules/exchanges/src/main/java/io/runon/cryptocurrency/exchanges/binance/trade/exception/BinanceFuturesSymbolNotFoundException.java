package io.runon.cryptocurrency.exchanges.binance.trade.exception;

public class BinanceFuturesSymbolNotFoundException extends Exception{
    public BinanceFuturesSymbolNotFoundException(String symbol) {
        super(String.format("Binance Futures symbol [%s] not found.",symbol));
    }
}
