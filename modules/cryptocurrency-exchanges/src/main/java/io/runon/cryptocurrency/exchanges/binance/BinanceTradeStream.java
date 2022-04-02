package io.runon.cryptocurrency.exchanges.binance;

import io.runon.cryptocurrency.exchanges.ExchangeWebSocketListener;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.json.JSONObject;

/**
 * 바이낸스 거래정보 수신
 * 바이낸스는 매수체결인지 매도체결인지 정보가 없음
 * 시세정보와 수량만 확인가능
 * https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md#websocket-limits
 * @author macle
 */
@Slf4j
public abstract class BinanceTradeStream  <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    protected String wssAddress = "wss://stream.binance.com:9443/ws";


    public BinanceTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketListener webSocketListener = null;


    private String subscribeMessage = "{\"method\":\"SUBSCRIBE\",\"id\":1,\"params\":[\"btcusdt@aggTrade\"]}";

    /**
     * 기본값은 btc usdt 1d
     * {"method":"SUBSCRIBE","id":1,"params":["btcusdt@aggTrade"]}
     *         Gson gson = new Gson();
     *         JsonArray params = new JsonArray();
     *         params.add("btcusdt@aggTrade");
     *         JsonObject object = new JsonObject();
     *         object.addProperty("method", "SUBSCRIBE");
     *         object.addProperty("id", 1);
     *         object.add("params", params);
     *         System.out.println(gson.toJson(object));
     * @param subscribeMessage subscribe message
     */
    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    @Override
    public void connect() {
        close();


        webSocketListener = new ExchangeWebSocketListener(streamId, wssAddress, subscribeMessage) {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if(isClose()){
                    return;
                }

                try {
                    JSONObject messageObj = new JSONObject(text);
                    if (messageObj.isNull("e")) {
                        log.debug(text);
                        return;
                    }

                    if(!messageObj.getString("e").equals("aggTrade")){
                        log.debug(text);
                        return;
                    }

                    addTrade(messageObj.getString("s"), new Trade(Trade.Type.NONE, messageObj.getBigDecimal("p"), messageObj.getBigDecimal("q"), System.currentTimeMillis()));


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
        return BinanceExchange.getMarketSymbol(cryptocurrencyId);
    }
}
