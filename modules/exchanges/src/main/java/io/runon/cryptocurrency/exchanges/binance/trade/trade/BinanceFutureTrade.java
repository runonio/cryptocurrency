package io.runon.cryptocurrency.exchanges.binance.trade.trade;

import com.binance.client.SyncRequestClient;
import com.binance.client.model.ResponseResult;
import com.binance.client.model.enums.NewOrderRespType;
import com.binance.client.model.enums.OrderSide;
import com.binance.client.model.enums.OrderType;
import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Order;
import com.binance.client.model.trade.Position;
import io.runon.cryptocurrency.exchanges.binance.trade.api.BinanceApiManager;
import io.runon.cryptocurrency.exchanges.binance.trade.exception.BinanceFuturesOrderLowerException;
import io.runon.cryptocurrency.exchanges.binance.trade.exception.BinanceFuturesSymbolNotFoundException;
import io.runon.cryptocurrency.exchanges.binance.trade.information.AccountInformationSupport;
import io.runon.cryptocurrency.exchanges.binance.trade.information.ExchangeInformationSupport;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class BinanceFutureTrade {

    private static final SyncRequestClient api = BinanceApiManager.getInstance().getApi();
    private static final AccountInformationSupport account = AccountInformationSupport.getInstance();
    private static final ExchangeInformationSupport exchange = ExchangeInformationSupport.getInstance();

    /**
     * 시장가 Long 주문,  최소금액 자리수를 알 경우 계산 없이 빠르게 주문
     * @param symbol 심볼
     * @param quantity 수량
     * @return 주문정보
     * @throws BinanceFuturesSymbolNotFoundException 심볼을 못찾은 경우
     * @throws BinanceFuturesOrderLowerException 주문 가격이 낮은 경우
     */
    public static Order longOrderByMarket(String symbol, BigDecimal quantity) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {
        return postOrder(symbol, null, quantity, null, null, OrderSide.BUY);
    }

    /**
     * 지정가 Long 주문,  최소금액 자리수를 알 경우 계산 없이 빠르게 주문
     * @param symbol  심볼
     * @param quantity 수량
     * @param price 가격, 생략시 시장가로 주문
     * @return 주문정보
     * @throws BinanceFuturesSymbolNotFoundException 심볼을 못찾은 경우
     * @throws BinanceFuturesOrderLowerException 주문 가격이 낮은 경우
     */
    public static Order longOrder(String symbol, BigDecimal quantity, String price) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {
        return postOrder(symbol, null, quantity, null, price, OrderSide.BUY);
    }

    /**
     * 지정가 Long 주문, 최소금액 자리수를 계산 후 주문
     * @param symbol 심볼
     * @param dollar 달러 문자열
     * @param price 가격, 생략시 시장가로 주문
     * @return 주문정보
     * @throws BinanceFuturesSymbolNotFoundException 심볼을 못찾은 경우
     * @throws BinanceFuturesOrderLowerException 주문 가격이 낮은 경우
     */
    public static Order dollarLongOrder(String symbol, String dollar, String price) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {
        return postOrder(symbol, new BigDecimal(dollar), null, null, price, OrderSide.BUY);
    }

    /**
     * 시장가 Short 주문,  최소금액 자리수를 알 경우 계산 없이 빠르게 주문
     * @param symbol 심볼
     * @param quantity 수량
     * @return 주문정보
     * @throws BinanceFuturesSymbolNotFoundException 심볼을 못찾은 경우
     * @throws BinanceFuturesOrderLowerException 주문 가격이 낮은 경우
     */
    public static Order shortOrderByMarket(String symbol, BigDecimal quantity) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {
        return postOrder(symbol, null, quantity, null, null, OrderSide.SELL);
    }

    /**
     * 지정가 Short 주문,  최소금액 자리수를 알 경우 계산 없이 빠르게 주문
     * @param symbol 심볼
     * @param quantity 수량
     * @param price 가격, 생략시 시장가 주문
     * @return 주문정보
     * @throws BinanceFuturesSymbolNotFoundException 심볼을 못찾은 경우
     * @throws BinanceFuturesOrderLowerException 주문 가격이 낮은 경우
     */
    public static Order shortOrder(String symbol, BigDecimal quantity, String price) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {
        return postOrder(symbol, null, quantity, null, price, OrderSide.SELL);
    }

    /**
     * 지정가 Short 주문,  최소금액 자리수를 계산 후 주문
     * @param symbol 심볼
     * @param dollar 달러
     * @param price 가격, 생략시 시장가 주문
     * @return 주문정보
     * @throws BinanceFuturesSymbolNotFoundException 심볼을 못찾은 경우
     * @throws BinanceFuturesOrderLowerException 주문 가격이 낮은 경우
     */
    public static Order dollarShortOrder(String symbol, String dollar, String price) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {
        return postOrder(symbol, new BigDecimal(dollar), null, null, price, OrderSide.SELL);
    }

    /**
     * 시장가 포지션 종료 (청산)
     * @param symbol 심볼
     * @return 주문정보
     */
    public static Order closeAllPositionsByMarket(String symbol){
        return closeAllPositions(symbol, null);
    }

    /**
     * 포지션 종료 (청산)
     * @param symbol 심볼
     * @param price 가격, 생략시 시장가 주문
     * @return 주문정보
     */
    public static Order closeAllPositions(String symbol, String price){
        AccountInformation accountInformation = account.getLiveAccountInformation();

        BigDecimal quantity = new BigDecimal(0);
        List<Position> positions = accountInformation.getPositions();
        for (Position position : positions) {
            if(position.getSymbol().equals(symbol)){
                quantity = position.getPositionAmt();
                break;
            }
        }

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

        log.info("["+symbol+"] 청산 : 가격["+price+"] 시장가여부["+(price == null)+"] 수량["+quantity.toPlainString()+"] 롱/숏["+(orderSide.name())+"]");

        return api.postOrder(symbol, orderSide, null,  price == null ?  OrderType.MARKET :  OrderType.LIMIT, null,
                quantity.toString(), price, "true", null, null, null, NewOrderRespType.RESULT);
    }

    /**
     * 마켓 주문
     * @param symbol 심볼
     * @param dollar 달러, quantity를 미 입력시 필수 입력
     * @param quantity 수량, 입력시 dollor 입력 값은 무시
     * @param orderId 주문식별ID (유니크한 값으로 이용)
     * @param price 가격, 생략시 시장가 주문
     * @param orderSide 주문유형 BUY / SELL
     * @return 주문정보
     * @throws BinanceFuturesSymbolNotFoundException 심볼을 못찾은 경우
     * @throws BinanceFuturesOrderLowerException 주문 가격이 낮은 경우
     */
    public static Order postOrder(String symbol, BigDecimal dollar, BigDecimal quantity, String orderId, String price, OrderSide orderSide) throws BinanceFuturesSymbolNotFoundException, BinanceFuturesOrderLowerException {
        if(quantity == null) {
            quantity = exchange.getQuantityPrice(symbol, dollar);
        }

        log.info("["+symbol+"] 주문 : 가격["+price+"] 시장가여부["+(price == null)+"] 수량["+quantity.toString()+"] 롱/숏["+(orderSide.name())+"]");

        return api.postOrder(symbol, orderSide, null, price == null ?  OrderType.MARKET :  OrderType.LIMIT , null,
                quantity.toString(), price, null, orderId, null, null, NewOrderRespType.RESULT);
    }

    /**
     * 체결 되지 않은 지정가 주문 내역 검색
     * @param symbol 심볼
     * @return 전체 주문정보
     */
    public static List<Order> getOpenOrders(String symbol){
        return api.getOpenOrders(symbol);
    }

    /**
     * 체결 되지 않은 모든 주문 취소
     * @param symbol 심볼
     * @return 결과값
     */
    public static ResponseResult cancelAllOrders(String symbol){
        return api.cancelAllOpenOrder(symbol);
    }


}
