package example.binance;

import example.CryptocurrencyImpl;
import io.runon.cryptocurrency.exchanges.binance.BinanceCandleStream;
import io.runon.cryptocurrency.exchanges.binance.BinanceMarketSymbol;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

/**
 * 바이낸스 실시간 구현체 예제
 * @author macle
 */
public class BinanceCandleStreamImpl extends BinanceCandleStream<CryptocurrencyImpl> {
    /**
     * @param streamId 아이디 (자유지정, 중복안됨)
     */
    public BinanceCandleStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyImpl(BinanceMarketSymbol.getMarketSymbol(cryptocurrencyId));
    }


    public static void main(String[] args) {
        new BinanceCandleStreamImpl("binance_candle").connect();
        new DataStreamKeepAliveService().start();
    }
}
