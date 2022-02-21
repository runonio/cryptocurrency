package io.runon.cryptocurrency.exchanges.kraken;

import com.seomse.commons.utils.ExceptionUtil;
import io.runon.cryptocurrency.exchanges.ExchangeWebSocketListener;
import io.runon.cryptocurrency.exchanges.TradeConverter;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.json.JSONObject;

/**
 * Kraken 선물 거래소 실시간 거래정보 stream
 * https://support.kraken.com/hc/en-us/articles/360022839491-API-URLs
 * @author macle
 */
@Slf4j
public abstract class KrakenFuturesTradeStream <T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public KrakenFuturesTradeStream(String streamId) {
        super(streamId);
    }

    private ExchangeWebSocketListener webSocketListener;

    private String subscribeMessage = "{\"event\":\"subscribe\",\"feed\":\"trade\",\"product_ids\":[\"PI_XBTUSD\"]}";
    /**
     *
     * 기본값 [{"ticket":"price"},{"type":"ticker","codes":["KRW-BTC"]}]
     * @param subscribeMessage subscribe message example: [{"ticket":"price"},{"type":"ticker","codes":["KRW-BTC","KRW-ETH"]}]
     */
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

        webSocketListener = new ExchangeWebSocketListener(streamId, "wss://futures.kraken.com/ws/v1", subscribeMessage) {
            @Override
            public void onMessage(WebSocket webSocket, String text) {

                if(isClose()){
                    return;
                }
                //거래량이 적어서 메시지오면 최근메시지로 기록함
                lastTime = System.currentTimeMillis();
                try {
                    JSONObject object = new JSONObject(text);

                    if(object.has("event") ){
                        return;
                    }

                    if(!object.getString("feed").equals("trade")){
                        return;
                    }

                    String id = object.getString("product_id");

                    Trade.Type type;

                    String side = object.getString("side");

                    if(side.equals("buy")){
                        type = Trade.Type.BUY;
                    }else if(side.equals("sell")){
                        type = Trade.Type.SELL;
                    }else{
                        return;
                    }
                    Trade trade = new Trade(type, object.getBigDecimal("price"), object.getBigDecimal("qty"), System.currentTimeMillis());
                    if(converter != null){
                        converter.convert(trade);
                    }
                    addTrade(id, trade);

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

        int index = cryptocurrencyId.indexOf('_');

        if(index != -1){
            cryptocurrencyId = cryptocurrencyId.substring(index+1);
        }

        MarketSymbol marketSymbol = new MarketSymbol();
        marketSymbol.setMarket("USD");
        marketSymbol.setSymbol(cryptocurrencyId.substring(0, cryptocurrencyId.length()-3));
        return marketSymbol;
    }
}
