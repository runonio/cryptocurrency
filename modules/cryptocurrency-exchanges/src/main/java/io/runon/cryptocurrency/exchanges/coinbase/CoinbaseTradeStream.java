package io.runon.cryptocurrency.exchanges.coinbase;

import io.runon.commons.utils.ExceptionUtils;
import io.runon.cryptocurrency.exchanges.DelimiterMarketSymbol;
import io.runon.cryptocurrency.exchanges.ExchangeWebSocketListener;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.json.JSONObject;

/**
 * 코인베이스 실시간 거래정보
 * https://docs.cloud.coinbase.com/exchange/docs/overview
 * @author macle
 */
@Slf4j
public abstract class CoinbaseTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public CoinbaseTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketListener webSocketListener = null;

    private String subscribeMessage = "{\"type\":\"subscribe\",\"channels\":[{\"name\":\"ticker\",\"product_ids\":[\"BTC-USD\"]}]}";

    /**
     *
     * 기본값 {"type":"subscribe","name":"ticker","channels":[{"product_ids":["BTC-USD"]}]}
     * @param subscribeMessage subscribe message example: {"type":"subscribe","channels":[{"name":"ticker","product_ids":["BTC-USD","ETH-USD"]}]}
     */
    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    @Override
    public void connect() {
        close();

        webSocketListener = new ExchangeWebSocketListener(streamId, "wss://ws-feed.exchange.coinbase.com", subscribeMessage) {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if(isClose()){
                    return;
                }


                try {
                    JSONObject object = new JSONObject(text);

                    if (!object.getString("type").equals("ticker")) {
                        log.debug(text);
                        return;
                    }

                    String side = object.getString("side").toLowerCase();
                    Trade.Type tradeType;
                    if(side.equals("buy")){
                        tradeType = Trade.Type.BUY;
                    }else if(side.equals("sell")){
                        tradeType = Trade.Type.SELL;
                    }else{
                        log.error("side check: " + side);
                        return;
                    }

                    String id = object.getString("product_id");
                    Trade trade = new Trade(tradeType, object.getBigDecimal("price"),object.getBigDecimal("last_size"), System.currentTimeMillis());
                    addTrade(id, trade);
                }catch(Exception e){
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        };

        webSocketListener.connect();
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return DelimiterMarketSymbol.leftSymbol("-",cryptocurrencyId);
    }

    @Override
    public void close(){
        try {if(webSocketListener != null) {webSocketListener.close();webSocketListener = null;}} catch (Exception ignore){}
    }
}
