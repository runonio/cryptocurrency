package io.runon.cryptocurrency.exchanges;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebSocketHandler
 * springframework
 * @author macle
 */
@Slf4j
public abstract class ExchangeWebSocketHandler implements WebSocketHandler {

    protected WebSocketSession webSocketSession = null;

    private final String id;

    private final String wssAddress;

    public ExchangeWebSocketHandler(String id, String wssAddress){
        this.id = id;
        this.wssAddress = wssAddress;
    }

    private String subscribeMessage = null;
    public ExchangeWebSocketHandler(String id, String wssAddress, String subscribeMessage){
        this.id = id;
        this.wssAddress = wssAddress;
        this.subscribeMessage = subscribeMessage;
    }

    private String [] subscribeMessages = null;
    public ExchangeWebSocketHandler(String id, String wssAddress, String [] subscribeMessages){
        this.id = id;
        this.wssAddress = wssAddress;
        this.subscribeMessages = subscribeMessages;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketSession = session;
        log.debug("afterConnectionEstablished " + session.getId() + ", id: " + id);
        if(subscribeMessage != null) {
            webSocketSession.sendMessage(new TextMessage(subscribeMessage));
        }

        if(subscribeMessages != null && subscribeMessages.length > 0){

            if(subscribeMessage != null){try {Thread.sleep(500);} catch (InterruptedException ignore) {}}

            webSocketSession.sendMessage(new TextMessage(subscribeMessages[0]));
            for (int i = 1; i <subscribeMessages.length; i++) {
                try {Thread.sleep(500);} catch (InterruptedException ignore) {}

                webSocketSession.sendMessage(new TextMessage(subscribeMessages[i]));
            }
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)  {
        log.error("handleTransportError " + exception.getMessage() + ", id: " + id);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        // 클로즈 이벤트가 와도 종료가 안되는경우가 있음을 발견
        // DataStreamKeepAliveService 에서 종합처리
        log.info("afterConnectionClosed " + session.getId() + " closeStatus " +closeStatus.toString()+  ", id: " + id);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        //여기를 재구현
        String data = message.getPayload().toString();
        log.info(id + " handle message: " + data);
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

            WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
            webSocketContainer.setDefaultMaxTextMessageBufferSize(1024*1024);

            ListenableFuture<WebSocketSession> listenableFuture =
                    new StandardWebSocketClient(webSocketContainer).doHandshake(this, headers, uri);

            listenableFuture.addCallback(
                    result -> webSocketSession = result, ex -> log.error("WebSocketClient connect failed, error:{}, type{}, id: " + id , ex.getMessage(), ex.getClass().getCanonicalName()));
        } catch (URISyntaxException e) {
            log.error("server url syntax error:{}, type:{}", e.getMessage(), e.getClass().getCanonicalName());
        } catch (Exception e) {
            log.error("WebsocketClient init error:{}, type:{}", e.getMessage(), e.getClass().getCanonicalName());
        }
    }

    private boolean isClose = false;
    // close 되어도 메시지가 들어오는 경우가 있음 방어소스
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

    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    public void setSubscribeMessages(String[] subscribeMessages) {
        this.subscribeMessages = subscribeMessages;
    }
}
