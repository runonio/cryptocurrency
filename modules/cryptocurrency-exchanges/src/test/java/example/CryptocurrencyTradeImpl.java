package example;

import io.runon.cryptocurrency.trading.CryptocurrencyTrade;
import io.runon.cryptocurrency.trading.MarketSymbol;
import io.runon.trading.Trade;
import io.runon.trading.data.csv.CsvTrade;

/**
 * 암호화폐 구현체 예제 (거래정보)
 * @author macle
 */
public class CryptocurrencyTradeImpl implements CryptocurrencyTrade {

    private long time = 0L;

    private final String id;

    private final String symbol;
    private final String market;

    public CryptocurrencyTradeImpl(MarketSymbol marketSymbol) {
        this.id = marketSymbol.getId();
        this.market = marketSymbol.getMarket();
        this.symbol = marketSymbol.getSymbol();
    }

    @Override
    public String getId() {
        return id;
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

    @Override
    public void addTrade(Trade trade) {
        time = System.currentTimeMillis();
        System.out.println(symbol + "-" + market + " add trade: " + CsvTrade.value(trade));
    }
}
