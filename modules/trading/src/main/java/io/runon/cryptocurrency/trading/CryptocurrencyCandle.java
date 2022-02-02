package io.runon.cryptocurrency.trading;

import io.runon.trading.technical.analysis.candle.TradeCandle;
/**
 * 트레이딩에 사용할 암호화페 캔들 활용
 * @author macle
 */
public interface CryptocurrencyCandle extends Cryptocurrency{
    void addCandle(TradeCandle tradeCandle);
}
