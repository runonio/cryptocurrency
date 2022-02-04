package io.runon.cryptocurrency.exchanges;

import io.runon.trading.Trade;
/**
 * 거래정보 변환기
 * @author macle
 */
public interface TradeConverter {
    void convert(Trade trade);
}
