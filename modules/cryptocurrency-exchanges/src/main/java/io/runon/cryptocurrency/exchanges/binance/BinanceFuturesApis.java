package io.runon.cryptocurrency.exchanges.binance;

import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Asset;
import com.binance.client.model.trade.Order;
import com.binance.client.model.trade.Position;
import io.runon.trading.account.FuturesPosition;
import io.runon.trading.account.FuturesPositionData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * 바이낸스 선물 api 에서 활용될 유틸관련 메소드 모음
 * @author macle
 */
public class BinanceFuturesApis {


    public static BigDecimal getUsdtAsset( AccountInformation accountInformation){
        return getAsset("USDT", accountInformation);
    }

    public static BigDecimal getUsdtCash( AccountInformation accountInformation){
        return getCash("USDT", accountInformation);
    }

    public static BigDecimal getAsset(String currency, AccountInformation accountInformation){
        Asset binanceAsset = getBinanceAsset(currency, accountInformation);
        if(binanceAsset == null){
            return BigDecimal.ZERO;
        }
        return binanceAsset.getMarginBalance();
    }

    public static BigDecimal getCash(String currency, AccountInformation accountInformation){
        Asset binanceAsset = getBinanceAsset(currency, accountInformation);
        if(binanceAsset == null){
            return BigDecimal.ZERO;
        }
        return binanceAsset.getMaxWithdrawAmount();
    }

    public static Asset getBinanceAsset(String currency, AccountInformation accountInformation){
        List<Asset> list = accountInformation.getAssets();
        Asset binanceAsset = null;
        for(Asset asset : list){
            if(asset.getAsset().equals(currency)){
                binanceAsset = asset;
                break;
            }
        }

        return binanceAsset;
    }

    /**
     * 체결가격 얻기
     * 시장가 주문에서 활용
     * @param order 바이낸스 주문정보
     * @return 체결가격
     */
    public static BigDecimal getTradePrice(Order order, int scale){

        return BigDecimal.ONE.divide(order.getExecutedQty(), MathContext.DECIMAL128).multiply(order.getCumQuote())
                .setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static Position getBinancePosition(String symbol, AccountInformation accountInformation){
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

    public static String getUsdtSymbol(String symbol){
        if(!symbol.endsWith("USDT")){
            symbol = symbol.toUpperCase();
            if(!symbol.endsWith("USDT")){
                symbol = symbol + "USDT";
            }
        }
        return symbol;
    }

    public static FuturesPosition getPosition(String symbol, AccountInformation accountInformation) {

        FuturesPositionData futuresPosition = new FuturesPositionData();
        futuresPosition.setSymbol(symbol);

        Position symbolPosition = BinanceFuturesApis.getBinancePosition(symbol, accountInformation);

        if(symbolPosition == null){
            return futuresPosition;
        }

        futuresPosition.setPrice(new BigDecimal(symbolPosition.getEntryPrice()));

        futuresPosition.setTradingPrice(symbolPosition.getInitialMargin());
        futuresPosition.setLeverage(symbolPosition.getLeverage());
        futuresPosition.setQuantity(symbolPosition.getPositionAmt());

        io.runon.trading.strategy.Position p ;
        if(futuresPosition.getPrice().compareTo(BigDecimal.ZERO) == 0){
            p = io.runon.trading.strategy.Position.NONE;
        }else{
            BigDecimal amt = symbolPosition.getPositionAmt();

            if(symbolPosition.getPositionAmt().compareTo(BigDecimal.ZERO) < 0 ) {
                p = io.runon.trading.strategy.Position.SHORT;
            }else if(symbolPosition.getPositionAmt().compareTo(BigDecimal.ZERO) > 0){
                p = io.runon.trading.strategy.Position.LONG;
            }else {
                p = io.runon.trading.strategy.Position.NONE;
            }
        }

        futuresPosition.setPosition(p);
        return futuresPosition;
    }
}
