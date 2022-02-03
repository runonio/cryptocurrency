package io.runon.cryptocurrency.exchanges.upbit;

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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 업비트 시세정보를 활용한 거래정보 수신
 * https://docs.upbit.com/docs/upbit-quotation-websocket
 */
@Slf4j
public abstract class UpbitTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {
    public UpbitTradeStream(String streamId) {
        super(streamId);
    }

    private WebSocketSession webSocketSession = null;

    private String message = "[{\"ticket\":\"price\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\"]}]";

    /**
     *
     * 기본값 [{"ticket":"price"},{"type":"ticker","codes":["KRW-BTC"]}]
     * @param message subscribe message example: [{"ticket":"price"},{"type":"ticker","codes":["KRW-BTC","KRW-ETH"]}]
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
                        webSocketSession.sendMessage(new TextMessage(message));
                    }
                }

                @Override
                public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                    try {
                        ByteBuffer heapByteBuffer = (ByteBuffer)message.getPayload();
                        byte [] array = heapByteBuffer.array();
                        JSONObject ticket = new JSONObject(new String(array, StandardCharsets.UTF_8));
                        if(ticket.getBoolean("is_trading_suspended")){
                            return;
                        }

                        Trade.Type type ;
                        if(ticket.getString("ask_bid").equals("BID")){
                            type = Trade.Type.BUY;
                        }else{
                            type = Trade.Type.SELL;
                        }

                        addTrade(ticket.getString("code"), new Trade(type, ticket.getBigDecimal("trade_price"), ticket.getBigDecimal("trade_volume"),System.currentTimeMillis()));

                    }catch(Exception ignore){}
                }

                @Override
                public void handleTransportError(WebSocketSession session, Throwable exception) {
                    log.error("handleTransportError " + exception.getMessage());
                }

                @Override
                public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
                    log.info("afterConnectionClosed " + session.getId());
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            };

            String serverUrl = "wss://api.upbit.com/websocket/v1";
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
    public void close(){
        try {
            if(webSocketSession != null) {
                webSocketSession.close();
                webSocketSession = null;
            }
        } catch (Exception ignore) {}
    }


    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return BarMarketSymbol.rightSymbol(cryptocurrencyId);
    }

}
