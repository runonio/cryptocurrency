package io.runon.cryptocurrency.exchanges.binance;

import io.runon.trading.account.FuturesAccount;
import io.runon.trading.account.FuturesHolding;

import java.math.BigDecimal;

/**
 * 바이낸스 백테스팅용 선물계좌
 * @author macle
 */
public class BinanceFuturesAccount extends FuturesAccount {

    //지정가 0.02% 시장가 0.04%
    //시장가로 테스트
    protected BigDecimal fee = new BigDecimal("0.04");

    public BinanceFuturesAccount(String id){
        super(id);
    }

    @Override
    public BigDecimal getBuyFee(FuturesHolding holding, BigDecimal price, BigDecimal volume) {
        return price.multiply(volume).multiply(fee);
    }

    @Override
    public BigDecimal getSellFee(FuturesHolding holding, BigDecimal price, BigDecimal volume) {
        return price.multiply(volume).multiply(fee);
    }

}
