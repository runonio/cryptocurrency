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
import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesApis;
import io.runon.trading.Trade;
import io.runon.trading.account.FuturesPosition;
import io.runon.trading.account.FuturesTradeAccount;
import io.runon.trading.exception.MinOrderException;
import io.runon.trading.exception.SymbolNotFoundException;
import io.runon.trading.order.MarketPriceOrder;
import io.runon.trading.order.MarketPriceOrderData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 바이낸스 선물 계좌
 * (지정가 주문은 예정)
 * @author macle
 */
public class BinanceFuturesUsdtAccount implements FuturesTradeAccount {

    private final SyncRequestClient syncRequestClient;
    private final String id;

    public BinanceFuturesUsdtAccount(String apiKey, String secretKey){
        RequestOptions options = new RequestOptions();
        syncRequestClient = SyncRequestClient.create(apiKey, secretKey, options);
        id = "binance_futures";
    }

    public BinanceFuturesUsdtAccount(String id, String apiKey, String secretKey){
        RequestOptions options = new RequestOptions();
        syncRequestClient = SyncRequestClient.create(apiKey, secretKey, options);
        this.id = id;
    }

    public BinanceFuturesUsdtAccount(SyncRequestClient syncRequestClient){
        this.syncRequestClient = syncRequestClient;
        id = "binance_futures";
    }

    public BinanceFuturesUsdtAccount(String id, SyncRequestClient syncRequestClient){
        this.syncRequestClient = syncRequestClient;
        this.id = id;
    }

    @Override
    public FuturesPosition getPosition(String symbol) {
        return BinanceFuturesApis.getPosition(symbol, syncRequestClient.getAccountInformation());
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
        FuturesPosition futuresPosition = getPosition(symbol);
        return futuresPosition.getLeverage();
    }

    @Override
    public MarketPriceOrder marketPriceOrder(String symbol, Trade.Type type, BigDecimal cash) {

        symbol = BinanceFuturesApis.getUsdtSymbol(symbol);

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

        BigDecimal leverage = getLeverage(symbol);
        BigDecimal quantity = cash.divide(currentPrice, quantityPrecision.intValue(), RoundingMode.DOWN);
        if(!(quantity.compareTo(BigDecimal.ZERO) > 0)){

            BigDecimal dividedNum = new BigDecimal(1);
            for (int i = 0; i < quantityPrecision; i++) {
                dividedNum = dividedNum.multiply(new BigDecimal(10));
            }

            String message = "Binance Future [" + symbol + "] price '" + cash + "', leverage '" + leverage + "' is lower. min dollar is ["
                    + currentPrice.divide(dividedNum, 2, RoundingMode.UP).stripTrailingZeros().toPlainString() +
                    "]";
            throw new MinOrderException(message);
        }

        Order order = syncRequestClient.postOrder(symbol, OrderSide.valueOf(type.toString()), null,  OrderType.MARKET  , null,
                quantity.toString(), null, null, null, null, null, NewOrderRespType.RESULT);

        MarketPriceOrderData marketPriceOrderData = new MarketPriceOrderData();
        marketPriceOrderData.setTradeType(type);
        marketPriceOrderData.setQuantity(order.getExecutedQty());
        marketPriceOrderData.setTradePrice(BinanceFuturesApis.getTradePrice(order,2));
        return marketPriceOrderData;
    }

    @Override
    public MarketPriceOrder closePosition(String symbol) {
        AccountInformation accountInformation = syncRequestClient.getAccountInformation();
        Position symbolPosition = BinanceFuturesApis.getBinancePosition(symbol, accountInformation);

        BigDecimal quantity = symbolPosition.getPositionAmt();

        if(quantity.compareTo(BigDecimal.ZERO) == 0){
            return MarketPriceOrder.EMPTY_MARKET_ORDER;
        }

        OrderSide orderSide;
        if(quantity.compareTo(new BigDecimal(0)) > 0){
            orderSide = OrderSide.BUY;
        } else {
            quantity = quantity.multiply(new BigDecimal(-1));
            orderSide = OrderSide.SELL;
        }

        Order order = syncRequestClient.postOrder(symbol, orderSide, null,  OrderType.MARKET , null,
                quantity.toString(), null, "true", null, null, null, NewOrderRespType.RESULT);

        MarketPriceOrderData marketPriceOrderData = new MarketPriceOrderData();
        marketPriceOrderData.setTradeType(Trade.Type.valueOf(orderSide.toString()));
        marketPriceOrderData.setQuantity(order.getExecutedQty());
        marketPriceOrderData.setTradePrice(BinanceFuturesApis.getTradePrice(order,2));
        return marketPriceOrderData;
    }
}
