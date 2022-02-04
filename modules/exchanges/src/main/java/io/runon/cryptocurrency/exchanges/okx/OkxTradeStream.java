package io.runon.cryptocurrency.exchanges.okx;

import io.runon.cryptocurrency.exchanges.DelimiterMarketSymbol;
import io.runon.cryptocurrency.exchanges.ExchangeWebSocketHandler;
import io.runon.cryptocurrency.exchanges.TradeConverter;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.math.BigDecimal;

/**
 * okx 실시간 거래정보
 * https://www.okx.com/docs-v5/en/#websocket-api-subscribe
 * @author macle
 */
public abstract class OkxTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public OkxTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketHandler webSocketHandler = null;

    private String subscribeMessage = "{\"op\":\"subscribe\",\"args\":[{\"channel\":\"trades\",\"instId\":\"BTC-USDT\"}]}";

    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    private TradeConverter converter = null;

    public void setConverter(TradeConverter converter) {
        this.converter = converter;
    }

    @Override
    public void connect() {
        close();
        //noinspection NullableProblems
        webSocketHandler = new ExchangeWebSocketHandler(streamId,"wss://ws.okex.com:8443/ws/v5/public", subscribeMessage){
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                try {
//                    {"event":"subscribe","arg":{"channel":"trades","instId":"BTC-USDT-220218"}}
//                    {"arg":{"channel":"trades","instId":"BTC-USDT-220218"},"data":[{"instId":"BTC-USDT-220218","tradeId":"12419","px":"39671.2","sz":"2","side":"sell","ts":"1643988102310"}]}
                    String text = message.getPayload().toString();

                    JSONObject object = new JSONObject(text);
                    if(object.isNull("arg")){
                        return ;
                    }

                    JSONObject arg = object.getJSONObject("arg");
                    if(arg.isNull("channel")){
                        return ;
                    }

                    if(!arg.getString("channel").equals("trades")){
                        return;
                    }

                    JSONArray data = object.getJSONArray("data");
                    for (int i = 0; i <data.length() ; i++) {
                        JSONObject tradeObj = data.getJSONObject(i);
                        String id = tradeObj.getString("instId");
                        BigDecimal volume =  tradeObj.getBigDecimal("sz");
                        Trade.Type type;
                        if(tradeObj.getString("side").equals("buy")){
                            type = Trade.Type.BUY;
                        }else{
                            type = Trade.Type.SELL;
                        }

                        Trade trade = new Trade(type, tradeObj.getBigDecimal("px"), volume, System.currentTimeMillis());
                        if(converter != null){
                            converter.convert(trade);
                        }

                        addTrade(id, trade);
                    }


                }catch(Exception ignore){}
            }
        };

        webSocketHandler.connect();
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return DelimiterMarketSymbol.getMarketSymbol("-", cryptocurrencyId, 1, 0);
    }

    @Override
    public void close(){
        try {if(webSocketHandler != null) {webSocketHandler.close();webSocketHandler = null;}} catch (Exception ignore){}
    }
}
