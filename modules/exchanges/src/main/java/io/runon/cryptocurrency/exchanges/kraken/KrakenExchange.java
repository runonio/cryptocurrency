package io.runon.cryptocurrency.exchanges.kraken;

import com.seomse.crawling.core.http.HttpUrl;

/**
 * Kraken 실시간 거래정보
 * 현물
 * https://docs.kraken.com/websockets/#message-trade
 * 선물
 * https://support.kraken.com/hc/en-us/articles/360022839491-API-URLs
 * @author macle
 */
public class KrakenExchange {

    /**
     * @return tickers 현물거래소
     */
    public static String getTickers(){
        return HttpUrl.get("https://www.kraken.com/api/internal/cryptowatch/markets/assets?asset=USD&limit=200&assetName=new");
    }

    public static void main(String[] args) {

    }

}
