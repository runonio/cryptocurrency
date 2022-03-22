package io.runon.cryptocurrency.exchanges;

import io.runon.trading.Trade;

import java.math.MathContext;

/**
 * 선물 거래소중 달러가 거래량인경우 달러에서 가격을 나누어서 거래량을 구한함
 * @author macle
 */
public class UsdVolumeConverter implements TradeConverter{
    @Override
    public void convert(Trade trade) {
        trade.setTradingPrice(trade.getVolume());
        trade.setVolume(trade.getVolume().divide(trade.getPrice(), MathContext.DECIMAL128));
    }
}
