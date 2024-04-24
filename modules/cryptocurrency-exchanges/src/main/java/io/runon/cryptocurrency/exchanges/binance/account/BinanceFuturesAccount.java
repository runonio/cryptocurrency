package io.runon.cryptocurrency.exchanges.binance.account;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.NewOrderRespType;
import com.binance.client.model.enums.OrderSide;
import com.binance.client.model.enums.OrderType;
import com.binance.client.model.market.ExchangeInfoEntry;
import com.binance.client.model.market.ExchangeInformation;
import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Order;
import com.binance.client.model.trade.Position;
import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;
import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesApis;
import io.runon.trading.Trade;
import io.runon.trading.account.FuturesPosition;
import io.runon.trading.account.FuturesTradeAccount;
import io.runon.trading.exception.MinOrderException;
import io.runon.trading.exception.SymbolNotFoundException;
import io.runon.trading.order.MarketOrderTrade;
import io.runon.trading.order.MarketOrderTradeData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 바이낸스 선물 계좌
 * (지정가 주문은 예정)
 * @author macle
 */
public class BinanceFuturesAccount implements FuturesTradeAccount {

    private final SyncRequestClient syncRequestClient;
    private final String id;

    private final BigDecimal fee =  new BigDecimal("0.0004");

    //계좌에서 인지한가격, 알고있는 가격, 실제가격가 슬리피지가 생길 수 있는가격을 저장
    private final Map<String, BigDecimal> symbolPriceMap = new HashMap<>();

    private final Object symbolPriceLock = new Object();

    public void setPrice(String symbol, BigDecimal price){
        synchronized (symbolPriceLock){
            symbolPriceMap.put(symbol, price);
        }
    }

    public BigDecimal getPrice(String symbol){
        synchronized (symbolPriceLock){
            return symbolPriceMap.get(symbol);
        }
    }

    private String market = "USDT";



    public BinanceFuturesAccount(String apiKey, String secretKey){
        RequestOptions options = new RequestOptions();
        syncRequestClient = SyncRequestClient.create(apiKey, secretKey, options);
        id = "binance_futures";
    }

    public BinanceFuturesAccount(String id, String apiKey, String secretKey){
        RequestOptions options = new RequestOptions();
        syncRequestClient = SyncRequestClient.create(apiKey, secretKey, options);
        this.id = id;
    }

    public BinanceFuturesAccount(SyncRequestClient syncRequestClient){
        this.syncRequestClient = syncRequestClient;
        id = "binance_futures";
    }

    public BinanceFuturesAccount(String id, SyncRequestClient syncRequestClient){
        this.syncRequestClient = syncRequestClient;
        this.id = id;
    }

    @Override
    public FuturesPosition getFuturesPosition(String symbol) {
        return BinanceFuturesApis.getPosition(symbol, syncRequestClient.getAccountInformation());
    }

    public void setMarket(String market) {
        this.market = market;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BigDecimal getAssets() {
        AccountInformation accountInformation = syncRequestClient.getAccountInformation();
        return BinanceFuturesApis.getUsdtAsset(accountInformation);
    }

    @Override
    public BigDecimal getCash() {
        AccountInformation accountInformation = syncRequestClient.getAccountInformation();
        return BinanceFuturesApis.getUsdtCash(accountInformation);
    }

    public SyncRequestClient getSyncRequestClient() {
        return syncRequestClient;
    }

    public AccountInformation getAccountInformation(){
        return syncRequestClient.getAccountInformation();
    }

    @Override
    public void setLeverage(String symbol, BigDecimal leverage) {
        syncRequestClient.changeInitialLeverage(symbol, leverage.intValue());
    }

    @Override
    public BigDecimal getLeverage(String symbol) {
        FuturesPosition futuresPosition = getFuturesPosition(symbol);
        return futuresPosition.getLeverage();
    }

    @Override
    public BigDecimal getAvailableBuyPrice(String symbol) {
        BigDecimal price = getCash();
        FuturesPosition futuresPosition = getFuturesPosition(symbol);
        if(futuresPosition != null && futuresPosition.getPosition() == io.runon.trading.strategy.Position.SHORT){
            //실제 구매가 다 안되는 경우를 발견해서 금액을 줄임
            price = price.add(futuresPosition.getTradingPrice().multiply(new BigDecimal("0.9")));
        }

        return price.subtract(price.multiply(fee));
    }

    @Override
    public BigDecimal getAvailableSellPrice(String symbol) {
        BigDecimal price = getCash();
        FuturesPosition futuresPosition = getFuturesPosition(symbol);
        if(futuresPosition != null && futuresPosition.getPosition() == io.runon.trading.strategy.Position.LONG){
            //실제 구매가 다 안되는 경우를 발견해서 금액을 줄임
            price = price.add(futuresPosition.getTradingPrice().multiply(new BigDecimal("0.9")));
        }

        return price.subtract(price.multiply(fee));
    }

    @Override
    public MarketOrderTrade marketOrderQuantity(String symbol, Trade.Type type, BigDecimal quantity) {

        BigDecimal lastClosePrice;
        synchronized (symbolPriceLock){
            lastClosePrice =  symbolPriceMap.get(symbol);
        }

        return orderQuantity(symbol, type, quantity, lastClosePrice);
    }

    public MarketOrderTrade orderQuantity(String symbol, Trade.Type type, BigDecimal quantity, BigDecimal lastClosePrice) {
        symbol = BinanceExchange.getSymbolMarket(symbol, market);

        long orderTime = System.currentTimeMillis();

        Order order = syncRequestClient.postOrder(symbol, OrderSide.valueOf(type.toString()), null,  OrderType.MARKET  , null,
                quantity.toString(), null, null, null, null, null, NewOrderRespType.RESULT);

        long closeTime = System.currentTimeMillis();

        BigDecimal leverage = getLeverage(symbol);
        MarketOrderTradeData marketPriceOrderData = new MarketOrderTradeData();
        marketPriceOrderData.setTradeType(type);
        marketPriceOrderData.setQuantity(order.getExecutedQty());
        marketPriceOrderData.setTradePrice(BinanceFuturesApis.getTradePrice(order,2));

        marketPriceOrderData.setLastClosePrice(lastClosePrice);
        marketPriceOrderData.setFee(marketPriceOrderData.getTradePrice().multiply(fee));

        marketPriceOrderData.setOrderTime(orderTime);
        marketPriceOrderData.setCloseTime(closeTime);

        return marketPriceOrderData;
    }

    @Override
    public MarketOrderTrade marketOrderCash(String symbol, Trade.Type type, BigDecimal cash) {
        symbol = BinanceExchange.getSymbolMarket(symbol, market);

        ExchangeInformation exchangeInformation = syncRequestClient.getExchangeInformation();
        List<ExchangeInfoEntry> symbols = exchangeInformation.getSymbols();
        ExchangeInfoEntry symbolEntry = null;
        for (ExchangeInfoEntry entry : symbols) {
            if(entry.getSymbol().equals(symbol)){
                symbolEntry = entry;
                break;
            }
        }

        if(symbolEntry == null){
            throw new SymbolNotFoundException(symbol);
        }

        Long quantityPrecision = symbolEntry.getQuantityPrecision();
        BigDecimal currentPrice = syncRequestClient.getMarkPrice(symbol).get(0).getMarkPrice();
        BigDecimal quantity = cash.divide(currentPrice, quantityPrecision.intValue(), RoundingMode.DOWN);
        if(!(quantity.compareTo(BigDecimal.ZERO) > 0)){

            BigDecimal dividedNum = new BigDecimal(1);
            for (int i = 0; i < quantityPrecision; i++) {
                dividedNum = dividedNum.multiply(new BigDecimal(10));
            }

            String message = "Binance Future [" + symbol + "] price '" + cash + "' is lower. min dollar is ["
                    + currentPrice.divide(dividedNum, 2, RoundingMode.UP).stripTrailingZeros().toPlainString() +
                    "]";
            throw new MinOrderException(message);
        }

        return orderQuantity(symbol, type, quantity, currentPrice);
    }

    @Override
    public MarketOrderTrade closePosition(String symbol) {

        BigDecimal lastClosePrice;
        synchronized (symbolPriceLock){
            lastClosePrice =  symbolPriceMap.get(symbol);
        }

        AccountInformation accountInformation = syncRequestClient.getAccountInformation();
        Position symbolPosition = BinanceFuturesApis.getBinancePosition(symbol, accountInformation);

        BigDecimal quantity = symbolPosition.getPositionAmt();

        if(quantity.compareTo(BigDecimal.ZERO) == 0){
            return MarketOrderTrade.EMPTY_MARKET_ORDER;
        }

        OrderSide orderSide;
        if(quantity.compareTo(BigDecimal.ZERO) > 0){
            orderSide = OrderSide.SELL;
        } else {
            quantity = quantity.multiply(new BigDecimal(-1));
            orderSide = OrderSide.BUY;

        }

        long orderTime = System.currentTimeMillis();

        Order order = syncRequestClient.postOrder(symbol, orderSide, null,  OrderType.MARKET , null,
                quantity.toString(), null, "true", null, null, null, NewOrderRespType.RESULT);

        long closeTime = System.currentTimeMillis();

        MarketOrderTradeData marketPriceOrderData = new MarketOrderTradeData();
        marketPriceOrderData.setTradeType(Trade.Type.valueOf(orderSide.toString()));
        marketPriceOrderData.setQuantity(quantity);
        marketPriceOrderData.setTradePrice(BinanceFuturesApis.getTradePrice(order,2));

        marketPriceOrderData.setLastClosePrice(lastClosePrice);
        marketPriceOrderData.setFee(marketPriceOrderData.getTradePrice().multiply(fee));
        marketPriceOrderData.setOrderTime(orderTime);
        marketPriceOrderData.setCloseTime(closeTime);

        return marketPriceOrderData;
    }


//    @Override
//    public LimitOrderTrade[] limitOrderQuantity(String symbol, Trade.Type type, BigDecimal quantity, BigDecimal limitPrice) {
//        return new LimitOrderTrade[0];
//    }
//
//    @Override
//    public LimitOrderTrade[] limitOrderCash(String symbol, Trade.Type type, BigDecimal cash, BigDecimal limitPrice) {
//        return new LimitOrderTrade[0];
//    }
//
//    @Override
//    public LimitOrderTrade[] limitOrderCash(String symbol, Trade.Type type, BigDecimal cash, BigDecimal beginPrice, BigDecimal endPrice, BigDecimal priceGap) {
//        return new LimitOrderTrade[0];
//    }
}
