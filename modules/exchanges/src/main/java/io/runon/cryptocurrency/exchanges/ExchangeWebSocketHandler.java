package io.runon.cryptocurrency.exchanges;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebSocketHandler
 * @author macle
 */
@SuppressWarnings({"RedundantThrows", "NullableProblems"})
@Slf4j
public abstract class ExchangeWebSocketHandler implements WebSocketHandler {

    private WebSocketSession webSocketSession = null;

    private final String subscribeMessage;
    private final String wssAddress;

    public ExchangeWebSocketHandler(String wssAddress, String subscribeMessage){
        this.subscribeMessage = subscribeMessage;
        this.wssAddress = wssAddress;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketSession = session;
        log.debug("afterConnectionEstablished " + session.getId());
        webSocketSession.sendMessage(new TextMessage(subscribeMessage));
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("handleTransportError " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("afterConnectionClosed " + session.getId() + " closeStatus " +closeStatus.toString());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void connect(){
        try {
            URI uri = new URI(wssAddress);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            ListenableFuture<WebSocketSession> listenableFuture =
                    new StandardWebSocketClient().doHandshake(this, headers, uri);

            listenableFuture.addCallback(
                    result -> webSocketSession = result, ex -> log.error("WebSocketClient connect failed, error:{}, type{}", ex.getMessage(), ex.getClass().getCanonicalName()));
        } catch (URISyntaxException e) {
            log.error("server url syntax error:{}, type:{}", e.getMessage(), e.getClass().getCanonicalName());
        } catch (Exception e) {
            log.error("WebsocketClient init error:{}, type:{}", e.getMessage(), e.getClass().getCanonicalName());
        }
    }

    public void close(){
        try {
            if(webSocketSession != null) {
                webSocketSession.close();
                webSocketSession = null;
            }
        } catch (Exception ignore) {}
    }
}
