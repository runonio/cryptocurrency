package example.binance.trade;

import io.runon.cryptocurrency.exchanges.binance.trade.exception.BinanceFuturesOrderLowerException;
import io.runon.cryptocurrency.exchanges.binance.trade.exception.BinanceFuturesSymbolNotFoundException;
import io.runon.cryptocurrency.exchanges.binance.trade.trade.BinanceFutureTrade;


public class BinanceFutureTradeTest {
    public static void main(String [] args) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {

        // 달러가로 롱 주문
        BinanceFutureTrade.dollarLongOrder("BTCUSDT", "40", null);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 청산
        BinanceFutureTrade.closeAllPositions("BTCUSDT",null);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 달러가로 숏 주문
        BinanceFutureTrade.dollarShortOrder("BTCUSDT", "40", null);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 청산
        BinanceFutureTrade.closeAllPositions("BTCUSDT",null);

    }
}
