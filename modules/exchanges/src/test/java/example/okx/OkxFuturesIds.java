package example.okx;

import io.runon.cryptocurrency.exchanges.okx.OkxExchange;

/**
 * Okx 선물거래소 아이디 목록 얻기
 * @author macle
 */
public class OkxFuturesIds {
    public static void main(String[] args) {
        String [] ids = OkxExchange.getIds("BTC", "FUTURES");
        for(String id : ids){
            System.out.println(id);
        }
    }
}
