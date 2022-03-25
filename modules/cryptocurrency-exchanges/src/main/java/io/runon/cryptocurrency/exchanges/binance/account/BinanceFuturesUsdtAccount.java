package io.runon.cryptocurrency.exchanges.binance.account;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.NewOrderRespType;
import com.binance.client.model.enums.OrderSide;
import com.binance.client.model.enums.OrderType;
import com.binance.client.model.market.ExchangeInfoEntry;
import com.binance.client.model.market.ExchangeInformation;
import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Asset;
import com.binance.client.model.trade.Order;
import com.binance.client.model.trade.Position;
import io.runon.trading.Trade;
import io.runon.trading.account.FuturesPosition;
import io.runon.trading.account.FuturesPositionData;
import io.runon.trading.account.FuturesTradeAccount;
import io.runon.trading.exception.MinOrderException;
import io.runon.trading.exception.SymbolNotFoundException;
import io.runon.trading.order.MarketPriceOrder;
import io.runon.trading.order.MarketPriceOrderData;

import java.math.BigDecimal;
import java.math.MathContext;
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

    @Override
    public FuturesPositionData getPosition(String symbol) {
        return getPosition(symbol, syncRequestClient.getAccountInformation());
    }

    public static FuturesPositionData getPosition(String symbol, AccountInformation accountInformation) {

        symbol = getSymbol(symbol);

        if(!symbol.endsWith("USDT")){
            symbol = symbol.toUpperCase();
            if(!symbol.endsWith("USDT")){
                symbol = symbol + "USDT";
            }
        }

        FuturesPositionData futuresPosition = new FuturesPositionData();
        futuresPosition.setSymbol(symbol);

        Position symbolPosition = getBinancePosition(symbol, accountInformation);

        if(symbolPosition == null){
            return futuresPosition;
        }

        futuresPosition.setPrice(new BigDecimal(symbolPosition.getEntryPrice()));
        futuresPosition.setSize(symbolPosition.getInitialMargin());
        futuresPosition.setLeverage(symbolPosition.getLeverage());

        io.runon.trading.strategy.Position p ;
        if(futuresPosition.getPrice().compareTo(BigDecimal.ZERO) == 0){
            p = io.runon.trading.strategy.Position.NONE;
        }else{
            if(symbolPosition.getPositionAmt().compareTo(BigDecimal.ZERO) < 0 ) {
                p = io.runon.trading.strategy.Position.SHORT;
            }else{
                p = io.runon.trading.strategy.Position.LONG;
            }
        }

        futuresPosition.setPosition(p);
        return futuresPosition;
    }

    public static Position getBinancePosition(String symbol,  AccountInformation accountInformation){
        List<Position> positions = accountInformation.getPositions();
        Position symbolPosition = null;
        for(Position position : positions) {
            if (position.getSymbol().equals(symbol)) {
                symbolPosition = position;
                break;
            }
        }
        return symbolPosition;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public BigDecimal getAssets() {
        AccountInformation accountInformation = syncRequestClient.getAccountInformation();
        return getAssets(accountInformation);
    }

    public static BigDecimal getAssets(AccountInformation accountInformation){
        List<Asset> list = accountInformation.getAssets();
        Asset usdt = null;
        for(Asset asset : list){
            if(asset.getAsset().equals("USDT")){
                usdt = asset;
                break;
            }
        }

        if(usdt == null){
            return BigDecimal.ZERO;
        }

        return usdt.getMarginBalance();
    }

    @Override
    public BigDecimal getCash() {
        AccountInformation accountInformation = syncRequestClient.getAccountInformation();
        return getCash(accountInformation);
    }

    public static BigDecimal getCash(AccountInformation accountInformation){
        List<Asset> list = accountInformation.getAssets();
        Asset usdt = null;
        for(Asset asset : list){
            if(asset.getAsset().equals("USDT")){
                usdt = asset;
                break;
            }
        }

        if(usdt == null){
            return BigDecimal.ZERO;
        }

        return usdt.getMaxWithdrawAmount();
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

    public static String getSymbol(String symbol){
        if(!symbol.endsWith("USDT")){
            symbol = symbol.toUpperCase();
            if(!symbol.endsWith("USDT")){
                symbol = symbol + "USDT";
            }
        }
        return symbol;
    }

    @Override
    public MarketPriceOrder marketPriceOrder(String symbol, Trade.Type type, BigDecimal cash) {

        symbol = getSymbol(symbol);

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

        BigDecimal tradePrice = BigDecimal.ONE.divide(order.getExecutedQty(), MathContext.DECIMAL128).multiply(order.getCumQuote())
                .setScale(2,RoundingMode.HALF_UP).stripTrailingZeros();

        MarketPriceOrderData marketPriceOrderData = new MarketPriceOrderData();
        marketPriceOrderData.setTradePrice(tradePrice);
        return marketPriceOrderData;
    }

    @Override
    public MarketPriceOrder closePosition(String symbol) {
        AccountInformation accountInformation = syncRequestClient.getAccountInformation();
        Position symbolPosition = getBinancePosition(symbol, accountInformation);

        BigDecimal quantity = symbolPosition.getPositionAmt();

        OrderSide orderSide;
        if(quantity.compareTo(new BigDecimal(0)) > 0){
            orderSide = OrderSide.SELL;
        } else {
            quantity = quantity.multiply(new BigDecimal(-1));
            orderSide = OrderSide.BUY;
        }

        if(quantity.compareTo(BigDecimal.ZERO) == 0){
            return null;
        }

        Order order = syncRequestClient.postOrder(symbol, orderSide, null,  OrderType.MARKET , null,
                quantity.toString(), null, "true", null, null, null, NewOrderRespType.RESULT);
        BigDecimal tradePrice = BigDecimal.ONE.divide(order.getExecutedQty(), MathContext.DECIMAL128).multiply(order.getCumQuote())
                .setScale(2,RoundingMode.HALF_UP).stripTrailingZeros();

        MarketPriceOrderData marketPriceOrderData = new MarketPriceOrderData();
        marketPriceOrderData.setTradePrice(tradePrice);
        return marketPriceOrderData;
    }
}
