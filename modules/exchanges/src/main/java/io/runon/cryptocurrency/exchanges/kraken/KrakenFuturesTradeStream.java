package io.runon.cryptocurrency.exchanges.kraken;

import com.seomse.commons.utils.ExceptionUtil;
import io.runon.cryptocurrency.exchanges.DelimiterMarketSymbol;
import io.runon.cryptocurrency.exchanges.ExchangeWebSocketHandler;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
/**
 * Kraken 선물 거래소 실시간 거래정보 stream
 * https://support.kraken.com/hc/en-us/articles/360022839491-API-URLs
 * @author macle
 */
@Slf4j
public abstract class KrakenFuturesTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public KrakenFuturesTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketHandler webSocketHandler = null;

    private String subscribeMessage = "{\"event\":\"subscribe\",\"feed\":\"trade\",\"product_ids\":[\"PI_XBTUSD\"]}";
    /**
     *
     * 기본값 [{"ticket":"price"},{"type":"ticker","codes":["KRW-BTC"]}]
     * @param subscribeMessage subscribe message example: [{"ticket":"price"},{"type":"ticker","codes":["KRW-BTC","KRW-ETH"]}]
     */
    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    private WebSocket webSocket = null;
    private OkHttpClient client = null;

    @Override
    public void connect() {
        close();

        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.debug("onOpen response:" + getStreamId());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    System.out.println(text);
                }catch(Exception e){
                    log.error(ExceptionUtil.getStackTrace(e));
                }
            }
            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                log.debug("onClosing code:" + code +", reason:" + reason + ", " + getStreamId());
            }
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                log.error("onClosed code:" + code +", reason:" + reason  + ", " + getStreamId());
            }
        };

        client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://futures.kraken.com/ws/v1").build();

        webSocket = client.newWebSocket(request, webSocketListener);
        webSocket.send(subscribeMessage);

        new Thread(){
            public void run(){
                for(;;){
                    try {

                        webSocket.send("{'ping'}");
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();



//        //noinspection NullableProblems
//        webSocketHandler = new ExchangeWebSocketHandler(streamId,"wss://futures.kraken.com/ws/v1", subscribeMessage){
//            @Override
//            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
//
//                if(isClose()){
//                    return;
//                }
//
//                try {
//                    String data = message.getPayload().toString();
//                    System.out.println(data);
//
//                }catch(Exception e){e.printStackTrace();}
//            }
//        };
//
//        webSocketHandler.connect();
    }

    @Override
    public void close(){
        try {if(webSocketHandler != null) {webSocketHandler.close();webSocketHandler = null;}} catch (Exception ignore){}
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return DelimiterMarketSymbol.leftSymbol("/",cryptocurrencyId);
    }
}
