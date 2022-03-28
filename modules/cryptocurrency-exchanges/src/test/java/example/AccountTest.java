package example;

import com.seomse.commons.config.Config;
import io.runon.cryptocurrency.exchanges.binance.account.BinanceFuturesUsdtAccount;
import io.runon.trading.Trade;
import io.runon.trading.order.MarketPriceOrder;

import java.math.BigDecimal;

/**
 * @author macle
 */
public class AccountTest {
    public static void main(String[] args)  {
        String symbol = "BTCUSDT";

        BinanceFuturesUsdtAccount account = new BinanceFuturesUsdtAccount(Config.getConfig("binance.api.key"),Config.getConfig("binance.secret.key"));
        MarketPriceOrder marketPriceOrder = account.orderCash("BTCUSDT", Trade.Type.BUY, new BigDecimal(50));
        System.out.println(marketPriceOrder.getTradeType() + " " + marketPriceOrder.getTradePrice() + " " +marketPriceOrder.getQuantity() );

        System.out.println(account.getCash());


//        try{account.closePosition(symbol);}catch (Exception e){e.printStackTrace();}
    }
}
