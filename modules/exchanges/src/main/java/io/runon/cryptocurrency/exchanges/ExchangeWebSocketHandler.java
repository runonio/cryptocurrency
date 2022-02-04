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

    protected WebSocketSession webSocketSession = null;

    private final String subscribeMessage;
    private final String wssAddress;

    public ExchangeWebSocketHandler(String id, String wssAddress, String subscribeMessage){
        this.id = id;
        this.subscribeMessage = subscribeMessage;
        this.wssAddress = wssAddress;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketSession = session;
        log.debug("afterConnectionEstablished " + session.getId() + ", id: " + id);
        webSocketSession.sendMessage(new TextMessage(subscribeMessage));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("handleTransportError " + exception.getMessage() + ", id: " + id);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        // 클로즈 이벤트가 와도 종료가 안되는경우가 있음을 발견
        // DataStreamKeepAliveService 에서 종합처리
        log.info("afterConnectionClosed " + session.getId() + " closeStatus " +closeStatus.toString()+  ", id: " + id);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void connect(){
        try {
            isClose = false;
            URI uri = new URI(wssAddress);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            ListenableFuture<WebSocketSession> listenableFuture =
                    new StandardWebSocketClient().doHandshake(this, headers, uri);

            listenableFuture.addCallback(
                    result -> webSocketSession = result, ex -> log.error("WebSocketClient connect failed, error:{}, type{}, id: " + id , ex.getMessage(), ex.getClass().getCanonicalName()));
        } catch (URISyntaxException e) {
            log.error("server url syntax error:{}, type:{}", e.getMessage(), e.getClass().getCanonicalName());
        } catch (Exception e) {
            log.error("WebsocketClient init error:{}, type:{}", e.getMessage(), e.getClass().getCanonicalName());
        }
    }

    private boolean isClose = false;

    public boolean isClose() {
        return isClose;
    }

    public void close(){
        try {

            isClose = true;
            if(webSocketSession != null) {
                webSocketSession.close();
                webSocketSession = null;
            }

        } catch (Exception ignore) {}
    }

    private String id;

    public void setId(String id) {
        this.id = id;
    }

}
