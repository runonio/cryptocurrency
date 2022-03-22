package io.runon.cryptocurrency.exchanges.ftx;

import io.runon.cryptocurrency.exchanges.ExchangeWebSocketListener;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

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

    private ExchangeWebSocketListener webSocketListener = null;

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


        webSocketListener = new ExchangeWebSocketListener(streamId, "wss://ftx.com/ws", subscribeMessages) {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if(isClose()){
                    return;
                }
                try {
                    JSONObject obj = new JSONObject(text);
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

        webSocketListener.connect();
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return FtxExchange.getMarketSymbol(cryptocurrencyId);
    }

    @Override
    public void close(){
        try {if(webSocketListener != null) {webSocketListener.close();webSocketListener = null;}} catch (Exception ignore){}
    }
}
