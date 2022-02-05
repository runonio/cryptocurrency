package io.runon.cryptocurrency.exchanges.kraken;

import io.runon.cryptocurrency.exchanges.DelimiterMarketSymbol;
import io.runon.cryptocurrency.exchanges.ExchangeWebSocketHandler;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Kraken 현물거래소 실시간 거래정보 stream
 *  https://docs.kraken.com/websockets/#message-trade
 * @author macle
 */
public abstract class KrakenTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public KrakenTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketHandler webSocketHandler = null;

    private String subscribeMessage = "{\"event\":\"subscribe\",\"subscription\":{\"name\":\"trade\"},\"pair\":[\"XBT/USD\"]}";
    /**
     *
     * 기본값 {"event":"subscribe","subscription":{"name":"trade"},"pair":["XBT/USD"]}
     * @param subscribeMessage subscribe message example: {"event":"subscribe","subscription":{"name":"trade"},"pair":["XBT/USD"]}
     */
    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }


    @Override
    public void connect() {
        close();

        //noinspection NullableProblems
        webSocketHandler = new ExchangeWebSocketHandler(streamId,"wss://ws.kraken.com", subscribeMessage){
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {

                if(isClose()){
                    return;
                }

                try {
                    String data = message.getPayload().toString();
                    System.out.println(data);

                }catch(Exception e){e.printStackTrace();}
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
        return DelimiterMarketSymbol.leftSymbol("/",cryptocurrencyId);
    }
}
