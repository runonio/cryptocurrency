package example.kraken;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.kraken.KrakenTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

/**
 * Kraken 체결내용 예제
 * @author macle
 */
public class KrakenTradeStreamImpl extends KrakenTradeStream<CryptocurrencyTradeImpl> {

    public KrakenTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }

    public static void main(String[] args) {
        new KrakenTradeStreamImpl("kraken_trade").connect();
        new DataStreamKeepAliveService().start();
    }
}
