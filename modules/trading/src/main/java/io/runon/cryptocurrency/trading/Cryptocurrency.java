package io.runon.cryptocurrency.trading;

import io.runon.trading.Trade;
import io.runon.trading.technical.analysis.candle.TradeCandle;

/**
 * 트레이딩에 사용할 암호화페
 * @author macle
 */
public interface Cryptocurrency {

    void addCandle(TradeCandle tradeCandle);
    void addTrade(Trade trade);

    String getSymbol();
    BaseCurrency getBaseCurrency();

    long getLastTime();
}
