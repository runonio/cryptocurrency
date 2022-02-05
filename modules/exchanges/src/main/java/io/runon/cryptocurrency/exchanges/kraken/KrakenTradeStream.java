package io.runon.cryptocurrency.exchanges.kraken;

import com.seomse.commons.utils.ExceptionUtil;
import io.runon.cryptocurrency.exchanges.DelimiterMarketSymbol;
import io.runon.cryptocurrency.exchanges.ExchangeWebSocketListener;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.json.JSONArray;

/**
 * Kraken 현물거래소 실시간 거래정보 stream
 *  https://docs.kraken.com/websockets/#message-trade
 * @author macle
 */
@Slf4j
public abstract class KrakenTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public KrakenTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketListener webSocketListener;

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

        webSocketListener = new ExchangeWebSocketListener(streamId, "wss://ws.kraken.com", subscribeMessage) {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                if(isClose()){
                    return;
                }
                //거래량이 적어서 메시지오면 최근메시지로 기록함
                lastTime = System.currentTimeMillis();

                try {
                    if(!text.startsWith("[")){
                        return;
                    }

                    JSONArray array = new JSONArray(text);
                    if (!array.getString(2).equals("trade")) {
                        return;
                    }

                    String id = array.getString(3);
                    array = array.getJSONArray(1);

                    for (int i = 0; i < array.length() ; i++) {
                        JSONArray row = array.getJSONArray(i);
                        Trade.Type type;
                        String side = row.getString(3);

                        if(side.equals("b")){
                            type = Trade.Type.BUY;
                        }else if(side.equals("s")){
                            type = Trade.Type.SELL;
                        }else{
                            continue;
                        }
                        //시간정보는 쓰지 않음 내가 전달받은 시간으로 활용함
                        addTrade(id, new Trade(type, row.getBigDecimal(0), row.getBigDecimal(1), System.currentTimeMillis()));
                    }

//                    String side = dataList.get(3).getAsString();


                }catch(Exception e){
                    log.error(ExceptionUtil.getStackTrace(e));
                }
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
        return DelimiterMarketSymbol.leftSymbol("/",cryptocurrencyId);
    }
}
