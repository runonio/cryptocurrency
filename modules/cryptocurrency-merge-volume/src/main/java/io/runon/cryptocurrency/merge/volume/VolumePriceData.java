package io.runon.cryptocurrency.merge.volume;

import io.runon.trading.Price;
import io.runon.trading.Trade;
import io.runon.trading.Volume;
import io.runon.trading.technical.analysis.volume.Volumes;

import java.math.BigDecimal;

/**
 * 거래량 정보 관련데이터
 * @author macle
 */
public class VolumePriceData implements Volume, Price {

    BigDecimal price = null;

    BigDecimal volume = BigDecimal.ZERO;
    BigDecimal amount = BigDecimal.ZERO;

    BigDecimal buyVolume = BigDecimal.ZERO;
    BigDecimal sellVolume = BigDecimal.ZERO;

    public void addTrade(Trade trade){
        price = trade.getPrice();

        if(trade.getType() == Trade.Type.BUY) {
            buyVolume = buyVolume.add(trade.getVolume());
        }else {
            sellVolume = sellVolume.add(trade.getVolume());
        }
        volume = volume.add(trade.getVolume());
        amount = amount.add(trade.getAmount());
    }

    @Override
    public BigDecimal getVolume() {
        return volume;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public BigDecimal getVolumePower(){
        return Volumes.getVolumePower(buyVolume, sellVolume);
    }

    @Override
    public BigDecimal getClose() {
        return price;
    }
}
