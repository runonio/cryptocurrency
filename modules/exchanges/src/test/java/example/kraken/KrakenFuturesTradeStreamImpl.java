package example.kraken;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.UsdVolumeConverter;
import io.runon.cryptocurrency.exchanges.kraken.KrakenFuturesTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

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
        KrakenFuturesTradeStreamImpl krakenFuturesTradeStreamImpl = new KrakenFuturesTradeStreamImpl("kraken_futures_trade");
        krakenFuturesTradeStreamImpl.setConverter(new UsdVolumeConverter());
        krakenFuturesTradeStreamImpl.connect();
        new DataStreamKeepAliveService().start();
    }
}
