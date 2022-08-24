package example.binance;

import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;

/**
 * @author macle
 */
public class BinanceAllTicker24h {
    public static void main(String[] args) {

        String tickersJson = BinanceExchange.getTickers24h();
        System.out.println(tickersJson);
    }
}
