package example.upbit;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.upbit.UpbitTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

/**
 * 업비트 체결내용 예제
 * @author macle
 */
public class UpbitTradeStreamImpl extends UpbitTradeStream<CryptocurrencyTradeImpl> {
    public UpbitTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }

    public static void main(String[] args) {
        new UpbitTradeStreamImpl("upbit_trade").connect();
        new DataStreamKeepAliveService().start();
    }
}
