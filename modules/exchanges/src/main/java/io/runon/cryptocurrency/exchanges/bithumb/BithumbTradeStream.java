package io.runon.cryptocurrency.exchanges.bithumb;

import io.runon.cryptocurrency.exchanges.DelimiterMarketSymbol;
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
 * 빗썸 거래정보 수신
 * https://docs.upbit.com/docs/upbit-quotation-websocket
 * @author macle
 */
@Slf4j
public abstract class BithumbTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public BithumbTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketListener webSocketListener = null;

    private String subscribeMessage = "{\"type\":\"transaction\", \"symbols\":[\"BTC_KRW\"]}";

    /**
     *
     * 기본값 {"type":"transaction", "symbols":["BTC_KRW"]}
     * @param subscribeMessage subscribe message example:{"type":"transaction", "symbols":["BTC_KRW" , "ETH_KRW"]}
     */
    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    @Override
    public void connect() {
        close();


        webSocketListener = new ExchangeWebSocketListener(streamId, "wss://pubwss.bithumb.com/pub/ws", subscribeMessage) {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if(isClose()){
                    return;
                }

                try {
                    JSONObject obj = new JSONObject(text);
                    if (obj.isNull("type") || !obj.getString("type").equals("transaction")) {
                        return;
                    }

                    JSONArray array = obj.getJSONObject("content").getJSONArray("list");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        Trade.Type type ;
                        if(object.getString("buySellGb").equals("2")){
                            type = Trade.Type.BUY;
                        }else{
                            type = Trade.Type.SELL;
                        }

                        addTrade(object.getString("symbol"), new Trade(type, object.getBigDecimal("contPrice"), object.getBigDecimal("contQty"), System.currentTimeMillis()));
                    }

                }catch(Exception ignore){}
            }
        };

        webSocketListener.connect();
    }


    @Override
    public void close(){
        try {if(webSocketListener != null) {webSocketListener.close();webSocketListener = null;}} catch (Exception ignore){}
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return DelimiterMarketSymbol.leftSymbol("_", cryptocurrencyId);
    }

}
