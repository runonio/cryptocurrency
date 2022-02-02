package example;

import io.runon.cryptocurrency.trading.CryptocurrencyCandle;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.data.csv.CsvTrade;
import io.runon.trading.technical.analysis.candle.TradeCandle;

/**
 * 암호화폐 구현체 예제
 * @author macle
 */
public class CryptocurrencyCandleImpl implements CryptocurrencyCandle {
    private long time = 0L;

    private final String symbol;
    private final String market;

    public CryptocurrencyCandleImpl(MarketSymbol marketSymbol){
        this.market = marketSymbol.getMarket();
        this.symbol = marketSymbol.getSymbol();
    }

    @Override
    public void addCandle(TradeCandle tradeCandle) {
        time = System.currentTimeMillis();
        System.out.println("add candle: " + CsvCandle.value(tradeCandle));
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getMarket() {
        return market;
    }

    @Override
    public long getLastTime() {
        return time;
    }
}
