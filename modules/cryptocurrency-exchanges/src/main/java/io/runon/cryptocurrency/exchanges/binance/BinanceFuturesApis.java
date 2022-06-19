package io.runon.cryptocurrency.exchanges.binance;

import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Asset;
import com.binance.client.model.trade.Order;
import com.binance.client.model.trade.Position;
import com.seomse.crawling.core.http.HttpUrl;
import io.runon.trading.account.FuturesPosition;
import io.runon.trading.account.FuturesPositionData;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * 바이낸스 선물 api 에서 활용될 유틸관련 메소드 모음
 * @author macle
 */
public class BinanceFuturesApis {

    public static final String URL ="https://fapi.binance.com";


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

    /**
     * 미체결 약정
     * 문서정보: binance-docs.github.io/apidocs/futures/en/#open-interest
     * @param symbol BTCUSDT,
     * @return {"symbol":"BTCUSDT","openInterest":"98136.374","time":1655645629238}
     */
    public static String getOpenInterest(String symbol){
        return HttpUrl.get(URL + "/fapi/v1/openInterest?symbol=" + symbol);
    }

    /**
     * 문서정보 binance-docs.github.io/apidocs/futures/en/#open-interest-statistics
     * @param symbol BTCUSDT (필수)
     * @param period "5m","15m","30m","1h","2h","4h","6h","12h","1d" ( default 5m)
     * @param limit default 30, max 500
     * @param startTime unix time
     * @param endTime unix time
     * @return [
     *     {
     *          "symbol":"BTCUSDT",
     *           "longShortRatio":"1.8105",  // long/short account num ratio of top traders
     *           "longAccount": "0.6442",   // long account num ratio of top traders
     *           "shortAccount":"0.3558",   // long account num ratio of top traders
     *           "timestamp":"1583139600000"
     *
     *      },
     *
     *      {
     *
     *          "symbol":"BTCUSDT",
     *           "longShortRatio":"0.5576",
     *           "longAccount": "0.3580",
     *           "shortAccount":"0.6420",
     *           "timestamp":"1583139900000"
     *
     *         },
     *
     * ]
     */
    public static String getOpenInterestStatistics(String symbol, String period, Integer limit, Long startTime, Long endTime){
        return get("/futures/data/openInterestHist", symbol, period, limit, startTime, endTime);
    }

    /**
     *
     * 문서정보 binance-docs.github.io/apidocs/futures/en/#long-short-ratio
     * @param symbol BTCUSDT (필수)
     * @param period "5m","15m","30m","1h","2h","4h","6h","12h","1d" ( default 5m)
     * @param limit default 30, max 500
     * @param startTime unix time
     * @param endTime unix time
     * @return [
     *     {
     *          "symbol":"BTCUSDT",  // long/short account num ratio of all traders
     *           "longShortRatio":"0.1960",  //long account num ratio of all traders
     *           "longAccount": "0.6622",   // short account num ratio of all traders
     *           "shortAccount":"0.3378",
     *           "timestamp":"1583139600000"
     *
     *      },
     *
     *      {
     *
     *          "symbol":"BTCUSDT",
     *           "longShortRatio":"1.9559",
     *           "longAccount": "0.6617",
     *           "shortAccount":"0.3382",
     *           "timestamp":"1583139900000"
     *
     *         },
     *
     * ]
     */
    public static String getLongShortRatio(String symbol, String period, Integer limit, Long startTime, Long endTime){
        return get("/futures/data/globalLongShortAccountRatio", symbol, period, limit, startTime, endTime);
    }

    public static String getTopLongShortRatioAccount(String symbol, String period, Integer limit, Long startTime, Long endTime){
        return get("/futures/data/topLongShortAccountRatio", symbol, period, limit, startTime, endTime);
    }
    public static String getTopLongShortRatioPositions(String symbol, String period, Integer limit, Long startTime, Long endTime){
        return get("/futures/data/topLongShortPositionRatio", symbol, period, limit, startTime, endTime);
    }

    public static String get(String api, String symbol, String period, Integer limit, Long startTime, Long endTime){
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(api).append("?symbol=").append(symbol).append("&period=");
        sb.append(Objects.requireNonNullElse(period, "5m"));
        if(limit != null){
            sb.append("&").append("limit=").append(limit);
        }

        if(startTime != null){
            sb.append("&").append("startTime=").append(startTime);
        }

        if(endTime != null){
            sb.append("&").append("endTime=").append(endTime);
        }

        return HttpUrl.get(sb.toString());
    }

}
