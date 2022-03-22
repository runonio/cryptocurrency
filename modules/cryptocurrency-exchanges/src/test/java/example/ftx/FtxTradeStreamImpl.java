package example.ftx;

import example.CryptocurrencyTradeImpl;
import io.runon.cryptocurrency.exchanges.ftx.FtxTradeStream;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;
/**
 * ftx 체결내용 예제
 * @author macle
 */
public class FtxTradeStreamImpl extends FtxTradeStream<CryptocurrencyTradeImpl> {
    public FtxTradeStreamImpl(String streamId) {
        super(streamId);
    }

    @Override
    public CryptocurrencyTradeImpl newCryptocurrency(String cryptocurrencyId) {
        return new CryptocurrencyTradeImpl(getMarketSymbol(cryptocurrencyId));
    }

    public static void main(String[] args) {
        FtxTradeStreamImpl ftxTradeStreamImpl = new FtxTradeStreamImpl("ftx_trade");
        String [] messages = {
                "{\"channel\":\"trades\",\"op\":\"subscribe\",\"market\":\"BTC-PERP\"}"
                , "{\"channel\":\"trades\",\"op\":\"subscribe\",\"market\":\"BTC/USD\"}"
                , "{\"channel\":\"trades\",\"op\":\"subscribe\",\"market\":\"BTC/USDT\"}"
        };
        ftxTradeStreamImpl.setSubscribeMessage(messages);
        ftxTradeStreamImpl.connect();
        new DataStreamKeepAliveService().start();
    }

}
