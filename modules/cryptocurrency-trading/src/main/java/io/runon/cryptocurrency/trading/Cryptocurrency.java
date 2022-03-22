package io.runon.cryptocurrency.trading;

/**
 * 트레이딩에 사용할 암호화페
 * @author macle
 */
public interface Cryptocurrency {

    /**
     * 거래소별로 관리되는 아이디
     * @return 거래소에서 관리되는 아이디
     */
    String getId();
    
    /**
     * BTC
     * ETH
     * ADA
     * MAMA
     * SOL
     * LUNA
     * ...
     * @return symbol
     */
    String getSymbol();

    /**
     *   USD
     *   USDT
     *   BUSD
     *   KRW
     *   BTC
     *   ETH
     *   ...
     * @return market
     */
    String getMarket();

    /**
     *
     * @return unix time(Millisecond)
     */
    long getLastTime();
}
