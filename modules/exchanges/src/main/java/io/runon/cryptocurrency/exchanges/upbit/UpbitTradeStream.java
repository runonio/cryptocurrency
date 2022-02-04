package io.runon.cryptocurrency.exchanges.upbit;

import io.runon.cryptocurrency.exchanges.DelimiterMarketSymbol;
import io.runon.cryptocurrency.exchanges.ExchangeWebSocketHandler;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import org.json.JSONObject;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 업비트 시세정보를 활용한 거래정보 수신
 * https://docs.upbit.com/docs/upbit-quotation-websocket
 * @author macle
 */
public abstract class UpbitTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public UpbitTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketHandler webSocketHandler = null;

    private String subscribeMessage = "[{\"ticket\":\"price\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\"]}]";

    /**
     *
     * 기본값 [{"ticket":"price"},{"type":"ticker","codes":["KRW-BTC"]}]
     * @param subscribeMessage subscribe message example: [{"ticket":"price"},{"type":"ticker","codes":["KRW-BTC","KRW-ETH"]}]
     */
    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    @Override
    public void connect() {
        close();

        //noinspection NullableProblems
        webSocketHandler = new ExchangeWebSocketHandler("wss://api.upbit.com/websocket/v1", subscribeMessage){
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

                    addTrade(ticket.getString("code"), new Trade(type, ticket.getBigDecimal("trade_price"), ticket.getBigDecimal("trade_volume"), System.currentTimeMillis()));

                }catch(Exception ignore){}
            }
        };

        webSocketHandler.connect();
    }

    @Override
    public void close(){
        try {if(webSocketHandler != null) {webSocketHandler.close();webSocketHandler = null;}} catch (Exception ignore){}
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return DelimiterMarketSymbol.rightSymbol("-",cryptocurrencyId);
    }
}
