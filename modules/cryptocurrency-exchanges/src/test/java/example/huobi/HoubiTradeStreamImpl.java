package example.huobi;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.huobi.HuobiTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

/**
 * 후오비 체결내용 예제
 * @author macle
 */
public class HoubiTradeStreamImpl extends HuobiTradeStream<CryptocurrencyTradeImpl> {

    public HoubiTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }

    public static void main(String[] args) {
        new HoubiTradeStreamImpl("houbi_trade").connect();
        new DataStreamKeepAliveService().start();
    }
}
