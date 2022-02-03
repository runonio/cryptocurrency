package example.coinbase;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.coinbase.CoinbaseTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

/**
 * 코인베이스 체결내용 예제
 * @author macle
 */
public class CoinbaseTradeStreamImpl extends CoinbaseTradeStream<CryptocurrencyTradeImpl> {
    public CoinbaseTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }
    public static void main(String[] args) {
        new CoinbaseTradeStreamImpl("coinbase_trade").connect();
        new DataStreamKeepAliveService().start();
    }
}
