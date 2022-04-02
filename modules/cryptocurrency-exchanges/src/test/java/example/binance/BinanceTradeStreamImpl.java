package example.binance;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.binance.BinanceTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;


/**
 * @author macle
 */
public class BinanceTradeStreamImpl extends BinanceTradeStream<CryptocurrencyTradeImpl> {
    public BinanceTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }
    public static void main(String[] args) {
        new BinanceTradeStreamImpl("binance_trade").connect();
        new DataStreamKeepAliveService().start();
    }
}
