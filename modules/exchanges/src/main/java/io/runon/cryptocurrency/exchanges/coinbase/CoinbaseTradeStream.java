package io.runon.cryptocurrency.exchanges.coinbase;

import com.seomse.commons.utils.ExceptionUtil;
import io.runon.cryptocurrency.exchanges.BarMarketSymbol;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 코인베이스 실시간 거래정보
 * https://docs.cloud.coinbase.com/exchange/docs/overview
 * @author macle
 */
@Slf4j
public abstract class CoinbaseTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public CoinbaseTradeStream(String streamId) {
        super(streamId);
    }

    private WebSocketSession webSocketSession = null;

    private String message = "{\"type\":\"subscribe\",\"channels\":[{\"name\":\"ticker\",\"product_ids\":[\"BTC-USD\"]}]}";

    /**
     *
     * 기본값 {"type":"subscribe","name":"ticker","channels":[{"product_ids":["BTC-USD"]}]}
     * @param message subscribe message example: {"type":"subscribe","channels":[{"name":"ticker","product_ids":["BTC-USD","ETH-USD"]}]}
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void connect() {
        close();

        try {
            //noinspection NullableProblems
            WebSocketHandler webSocketHandler = new WebSocketHandler(){
                @Override
                public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                    synchronized (lock) {
                        webSocketSession = session;
                        log.debug("afterConnectionEstablished " + session.getId());
                        if(webSocketSession.isOpen()) {
                            webSocketSession.sendMessage(new TextMessage(message));
                        }
                    }
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {

                    try {
                        String data = message.getPayload().toString();

                        JSONObject object = new JSONObject(data);

                        if (!object.getString("type").equals("ticker")) {
                            log.debug(data);
                            return;
                        }

                        String side = object.getString("side").toLowerCase();
                        Trade.Type tradeType;
                        if(side.equals("buy")){
                            tradeType = Trade.Type.BUY;
                        }else if(side.equals("sell")){
                            tradeType = Trade.Type.SELL;
                        }else{
                            log.error("side check: " + side);
                            return;
                        }

                        String id = object.getString("product_id");
                        Trade trade = new Trade(tradeType, object.getBigDecimal("price"),object.getBigDecimal("last_size"), System.currentTimeMillis());
                        addTrade(id, trade);
                    }catch(Exception e){
                        log.error(ExceptionUtil.getStackTrace(e));
                    }
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) {
                    log.error("handleTransportError " + exception.getMessage());
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
                    log.info("afterConnectionClosed " + session.getId() + " closeStatus " +closeStatus.toString());
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            };

            String serverUrl = "wss://ws-feed.exchange.coinbase.com";
            URI uri = new URI(serverUrl);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            ListenableFuture<WebSocketSession> listenableFuture =
                    new StandardWebSocketClient().doHandshake(webSocketHandler, headers, uri);

            listenableFuture.addCallback(
                    result -> webSocketSession = result, ex -> log.error("WebSocketClient connect failed, error:{}, type{}", ex.getMessage(), ex.getClass().getCanonicalName()));

        } catch (URISyntaxException e) {
            log.error("server url syntax error:{}, type:{}", e.getMessage(), e.getClass().getCanonicalName());
        } catch (Exception e) {
            log.error("WebsocketClient init error:{}, type:{}", e.getMessage(), e.getClass().getCanonicalName());
        }
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return BarMarketSymbol.leftSymbol(cryptocurrencyId);
    }

    @Override
    public void close(){
        try {
            if(webSocketSession != null) {
                webSocketSession.close();
                webSocketSession = null;
            }
        } catch (Exception ignore) {}
    }



    public static void main(String[] args) {

    }
}
