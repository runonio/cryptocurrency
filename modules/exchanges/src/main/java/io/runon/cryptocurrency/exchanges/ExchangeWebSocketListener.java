package io.runon.cryptocurrency.exchanges;

import com.seomse.commons.utils.ExceptionUtil;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONObject;

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
    private final String wssAddress;

    public ExchangeWebSocketListener(String id, String wssAddress, String subscribeMessage){
        this.id = id;
        this.subscribeMessage = subscribeMessage;
        this.wssAddress = wssAddress;
    }

    private WebSocket webSocket = null;
    private OkHttpClient client = null;


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
        log.error("onClosed code:" + code +", reason:" + reason  + ", " + id);
    }


}
