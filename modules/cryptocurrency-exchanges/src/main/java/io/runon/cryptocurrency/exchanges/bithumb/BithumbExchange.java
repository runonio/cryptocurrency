package io.runon.cryptocurrency.exchanges.bithumb;

import io.runon.commons.http.HttpApis;

/**
 * 빗썸 거래소
 * https://apidocs.bithumb.com/docs/websocket_public
 *
 * type	구독 메시지 종류("ticker" / "transaction" / "orderbookdepth")	String
 * symbols	BTC_KRW, ETH_KRW, …	Array (String)
 * (optional) tickTypes	tick 종류 ("30M"/"1H"/"12H"/"24H"/"MID")	Array (String)
 *
 * 구독 메시지 종류("ticker" / "transaction" / "orderbookdepth")	String
 * 현재가(ticker)
 * {"type":"ticker", "symbols": ["BTC_KRW", "ETH_KRW"], "tickTypes": ["30M", "1H", "12H", "24H", "MID" ]}
 * 체결(transaction)
 * {"type":"transaction", "symbols":["BTC_KRW" , "ETH_KRW"]}
 * 변경호가(orderbookdepth)
 * {"type":"orderbookdepth", "symbols":["BTC_KRW" , "ETH_KRW"]}
 * Example Response (ticker)
 * author macle
 */
public class BithumbExchange {

    /**
     *
     * @param market KRW OR BTC OR ....
     * @return json object
     */
    public static String getTickers(String market){
        return HttpApis.getMessage("https://api.bithumb.com/public/ticker/ALL_" + market.toUpperCase());
    }

}
