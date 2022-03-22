package example.okx;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.UsdVolumeConverter;
import io.runon.cryptocurrency.exchanges.okx.OkxExchange;
import io.runon.cryptocurrency.exchanges.okx.OkxTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;

/**
 * Okx(과거 Okex) 체결내용 예제
 * @author macle
 */
public class OkxTradeStreamImpl extends OkxTradeStream<CryptocurrencyTradeImpl> {
    public OkxTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }

    public static void main(String[] args) {
        OkxTradeStreamImpl okxTradeStreamImpl = new OkxTradeStreamImpl("okx_trade");
//        okxTradeStreamImpl.setSubscribeMessage(OkxExchange.getTradeSubscribeMessage(OkxExchange.getIds("ETH","FUTURES")));
        okxTradeStreamImpl.setSubscribeMessage(OkxExchange.getTradeSubscribeMessage(OkxExchange.getIds("BTC","FUTURES")));
        okxTradeStreamImpl.setConverter(new UsdVolumeConverter());
        okxTradeStreamImpl.connect();
        new DataStreamKeepAliveService().start();
    }
}

