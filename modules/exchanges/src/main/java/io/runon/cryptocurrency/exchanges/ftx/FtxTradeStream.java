package io.runon.cryptocurrency.exchanges.ftx;

import com.seomse.commons.utils.ExceptionUtil;
import io.runon.cryptocurrency.exchanges.ExchangeWebSocketHandler;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * ftx 실시간 거래정보
 * https://docs.ftx.com/#websocket-api
 * @author macle
 */
@Slf4j
public abstract class FtxTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public FtxTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketHandler webSocketHandler = null;

    private String [] subscribeMessages = {"{\"channel\":\"trades\",\"op\":\"subscribe\",\"market\":\"BTC/USDT\"}"};

    /**
     *
     * {"channel":"trades","op":"subscribe","market":"BTC/USDT"}
     * @param subscribeMessage subscribe message example: {"channel":"trades","op":"subscribe","market":"BTC/USDT"}
     */
    public void setSubscribeMessage(String subscribeMessage) {
        String [] subscribeMessages = new String[1];
        subscribeMessages[0] = subscribeMessage;

        this.subscribeMessages = subscribeMessages;
    }
    /**
     *
     * {"channel":"trades","op":"subscribe","market":"BTC/USDT"}
     * @param subscribeMessages subscribe message example: {"channel":"trades","op":"subscribe","market":"BTC/USDT"}
     */
    public void setSubscribeMessage(String [] subscribeMessages) {
        this.subscribeMessages = subscribeMessages;
    }

    @Override
    public void connect() {
        close();
        //noinspection NullableProblems
        webSocketHandler = new ExchangeWebSocketHandler(streamId,"wss://ftx.com/ws", null){

            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                webSocketSession = session;
                log.debug("afterConnectionEstablished " + session.getId() + ", id: " + streamId);

                new Thread(() -> {
                    try {
                        webSocketSession.sendMessage(new TextMessage(subscribeMessages[0]));
                        for (int i = 1; i <subscribeMessages.length; i++) {
                            try {
                                //noinspection BusyWait
                                Thread.sleep(500);
                            } catch (InterruptedException ignore) {}
                            webSocketSession.sendMessage(new TextMessage(subscribeMessages[i]));
                        }
                    } catch (IOException e) {
                        log.error(ExceptionUtil.getStackTrace(e));
                    }

                    //noinspection ConditionalBreakInInfiniteLoop
                    for(;;){
                        if(!isConnect()){
                            break;
                        }
                        try {
                            webSocketSession.sendMessage(new TextMessage("{\"op\": \"ping\"}"));
                        } catch (IOException e) {
                            log.error(ExceptionUtil.getStackTrace(e));
                        }

                        try {
                            //noinspection BusyWait
                            Thread.sleep(5000L);
                        } catch (InterruptedException ignore) {}

                    }
                }).start();

            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                if(!isConnect()){
                    return;
                }

                try {

                    String jsonValue = message.getPayload().toString();
                    JSONObject obj = new JSONObject(jsonValue);
                    if(!obj.getString("type").equals("update")){
                        return;
                    }

                    String id = obj.getString("market");

                    JSONArray array = new JSONArray(obj.getJSONArray("data"));
                    for (int i = 0; i <array.length() ; i++) {
                        JSONObject data = array.getJSONObject(i);
                        Trade.Type type;
                        if(data.getString("side").equals("buy")){
                            type = Trade.Type.BUY;
                        }else{
                            type = Trade.Type.SELL;
                        }
                        addTrade(id, new Trade(type, data.getBigDecimal("price"), data.getBigDecimal("size"), System.currentTimeMillis()));
                    }

                }catch(Exception ignore){}
            }
        };

        webSocketHandler.connect();
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return FtxExchange.getMarketSymbol(cryptocurrencyId);
    }

    @Override
    public void close(){
        try {if(webSocketHandler != null) {webSocketHandler.close();webSocketHandler = null;}} catch (Exception ignore){}
    }
}
