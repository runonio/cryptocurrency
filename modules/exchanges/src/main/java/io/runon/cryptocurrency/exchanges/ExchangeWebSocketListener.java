package io.runon.cryptocurrency.exchanges;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

/**
 * WebSocketListener
 * okhttp3
 * @author macle
 */
@SuppressWarnings("NullableProblems")
@Slf4j
public class ExchangeWebSocketListener extends WebSocketListener {

    private final String id;
    private final String subscribeMessage;
    protected final String wssAddress;

    public ExchangeWebSocketListener(String id, String wssAddress, String subscribeMessage){
        this.id = id;
        this.subscribeMessage = subscribeMessage;
        this.wssAddress = wssAddress;
    }

    protected WebSocket webSocket = null;
    protected OkHttpClient client = null;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        log.debug("onOpen response:" + id);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        log.info(id + " on message: " + text);

    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        log.debug("onClosing code:" + code +", reason:" + reason + ", " + id);
    }
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        if(code == 0) {
            log.debug("onClosed code:" + code + ", reason:" + reason + ", " + id);
        }else{
            log.error("onClosed code:" + code + ", reason:" + reason + ", " + id);
        }
    }

    public void connect() {
        isClose = false;
        client = new OkHttpClient();
        Request request = new Request.Builder().url(wssAddress).build();
        webSocket = client.newWebSocket(request, this);
        webSocket.send(subscribeMessage);
    }

    protected boolean isClose = false;
    // close 되어도 메시지가 들어오는 경우가 있음 방어소스
    public boolean isClose() {
        return isClose;
    }

    public void close(){
        if(webSocket != null){
            try{webSocket.close(0, null); webSocket =null;}catch (Exception ignore){}
            try{client.dispatcher().executorService().shutdown(); client = null;}catch (Exception ignore){}
        }
        isClose = true;
    }
}
