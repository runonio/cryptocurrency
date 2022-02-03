package example.upbit;

import io.runon.cryptocurrency.exchanges.upbit.UpbitExchange;

/**
 * 업비트 subscribe message 생성 예제
 * @author macle
 */
public class UpbitSubscribeMessage {
    public static void main(String[] args) {
        System.out.println(UpbitExchange.getSubscribeMessage("btc","krw"));
    }
}
