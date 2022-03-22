package io.runon.cryptocurrency.exchanges.huobi;

import com.alibaba.fastjson.JSONObject;
import com.huobi.client.req.market.SubMarketTradeRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.model.market.MarketTradeEvent;
import com.huobi.service.huobi.HuobiMarketService;
import com.huobi.service.huobi.connection.HuobiWebSocketConnection;
import com.huobi.service.huobi.parser.market.MarketTradeEventParser;
import com.huobi.utils.InputChecker;
import com.huobi.utils.ResponseCallback;
import com.huobi.utils.SymbolUtils;
import com.seomse.commons.utils.ExceptionUtil;
import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.DataStreamTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 후오비 실시간 거래 정보
 * @author macle
 */
@Slf4j
public abstract class HuobiTradeStream<T extends CryptocurrencyTrade> extends DataStreamTrade<T> {

    public HuobiTradeStream(String streamId) {
        super(streamId);
    }

    @Override
    public MarketSymbol getMarketSymbol(String cryptocurrencyId) {
        return HoubiExchange.getMarketSymbol(cryptocurrencyId);
    }


    private String subscribeMessage = "btcusdt";

    /**
     * btcusdt,ethusdt
     * 기본값 btcusdt
     * @param subscribeMessage subscribe message example: btcusdt,ethusdt....
     */
    public void setSubscribeMessage(String subscribeMessage) {
        this.subscribeMessage = subscribeMessage;
    }

    private HuobiWebSocketConnection huobiWebSocketConnection = null;
    private boolean isErrorExtract = true;

    @Override
    public void connect() {
        isErrorExtract = true;
        close();

        try {
            subscribe(SubMarketTradeRequest.builder().symbol(subscribeMessage).build(), (tradeEvent) -> {

                String id =  tradeEvent.getCh().split("\\.")[1];
                tradeEvent.getList().forEach(marketTrade -> {

                    try {

                        Trade.Type tradeType;
                        String direction = marketTrade.getDirection();

                        if (direction.equals("buy")) {
                            tradeType = Trade.Type.BUY;
                        } else if (direction.equals("sell")) {
                            tradeType = Trade.Type.SELL;
                        } else {
                            log.error("direction check: " + direction);
                            return;
                        }
                        Trade trade = new Trade(tradeType, marketTrade.getPrice(), marketTrade.getAmount(), System.currentTimeMillis());
                        addTrade(id, trade);

                    }catch(Exception e){
                        if(isErrorExtract){
                            //로그파일이 어마무시해질것 예상해서 1개만 출력하기
                            isErrorExtract = false;
                            log.error(ExceptionUtil.getStackTrace(e));
                        }
                    }
                });
            });
        }catch (Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }

    private void subscribe(SubMarketTradeRequest request, ResponseCallback<MarketTradeEvent> callback){
        InputChecker.checker()
                .shouldNotNull(request.getSymbol(), "symbol");

        List<String> symbolList = SymbolUtils.parseSymbols(request.getSymbol());

        InputChecker.checker()
                .checkSymbolList(symbolList);

        List<String> commandList = new ArrayList<>(symbolList.size());
        symbolList.forEach(symbol -> {

            String topic = HuobiMarketService.WEBSOCKET_MARKET_TRADE_TOPIC
                    .replace("$symbol", symbol);

            JSONObject command = new JSONObject();
            command.put("sub", topic);
            command.put("id", System.nanoTime());
            commandList.add(command.toJSONString());
        });
        huobiWebSocketConnection = HuobiWebSocketConnection.createMarketConnection(new HuobiOptions(), commandList, new MarketTradeEventParser(), callback, false);

        huobiWebSocketConnection.setErrorCallback(obj -> log.error(obj.toString() + "\n" + "huobi error"));
    }

    @Override
    public void close(){
        if(huobiWebSocketConnection != null){
            try{huobiWebSocketConnection.close(); huobiWebSocketConnection = null;}catch (Exception e){log.error(ExceptionUtil.getStackTrace(e));}
        }
    }
}
