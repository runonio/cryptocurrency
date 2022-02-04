package example.bithumb;

import example.CryptocurrencyTradeImpl;
import example.coinbase.CoinbaseTradeStreamImpl;
import io.runon.cryptocurrency.exchanges.bithumb.BithumbTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

/**
 * 빗썸 체결내용 예제
 * @author macle
 */
public class BithumbTradeStreamImpl extends BithumbTradeStream<CryptocurrencyTradeImpl> {
    public BithumbTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }
    public static void main(String[] args) {
        new BithumbTradeStreamImpl("bithumb_trade").connect();
        new DataStreamKeepAliveService().start();
    }
}

