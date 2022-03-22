package example.coinbase;

import io.runon.cryptocurrency.exchanges.coinbase.CoinbaseExchange;

/**
 * 코인베이스 subscribe message 생성 예제
 * @author macle
 */
public class CoinbaseSubscribeMessage {
    public static void main(String[] args) {
        System.out.println(CoinbaseExchange.getSubscribeMessage("btc","usd"));
    }
}
