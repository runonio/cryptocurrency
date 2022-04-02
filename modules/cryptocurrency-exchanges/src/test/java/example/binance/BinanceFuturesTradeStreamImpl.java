package example.binance;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.binance.BinanceFuturesTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;
/**
 * @author macle
 */
public class BinanceFuturesTradeStreamImpl  extends BinanceFuturesTradeStream<CryptocurrencyTradeImpl> {
    public BinanceFuturesTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }
    public static void main(String[] args) {
        new BinanceTradeStreamImpl("binance_futures_trade").connect();
        new DataStreamKeepAliveService().start();
    }
}
