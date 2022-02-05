package example.kraken;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.kraken.KrakenFuturesTradeStream;

/**
 * Kraken 선물 체결내용 예제
 * @author macle
 */
public class KrakenFuturesTradeStreamImpl extends KrakenFuturesTradeStream<CryptocurrencyTradeImpl> {

    public KrakenFuturesTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }

    public static void main(String[] args) {
        new KrakenFuturesTradeStreamImpl("kraken_futures_trade").connect();
//        new DataStreamKeepAliveService().start();
    }
}
