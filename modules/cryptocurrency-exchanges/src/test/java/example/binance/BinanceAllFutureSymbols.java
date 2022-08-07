package example.binance;

import com.binance.client.model.market.MarkPrice;
import io.runon.cryptocurrency.exchanges.binance.BinanceExchange;

import java.util.List;

/**
 * @author macle
 */
public class BinanceAllFutureSymbols {
    public static void main(String[] args) {
        List<MarkPrice> list = BinanceExchange.getFuturesTickers();
        for(MarkPrice markPrice : list){
            System.out.println(markPrice.getSymbol());
        }
    }
}
