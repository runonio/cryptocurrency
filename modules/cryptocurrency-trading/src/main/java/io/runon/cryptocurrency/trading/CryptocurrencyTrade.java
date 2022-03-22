package io.runon.cryptocurrency.trading;

import io.runon.trading.Trade;

/**
 * 트레이딩에 사용할 암호화페 거래정보 활용
 * @author macle
 */
public interface CryptocurrencyTrade extends Cryptocurrency{

    void addTrade(Trade trade);
}
