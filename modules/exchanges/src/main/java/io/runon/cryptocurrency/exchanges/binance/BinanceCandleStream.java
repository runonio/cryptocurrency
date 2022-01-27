package io.runon.cryptocurrency.exchanges.binance;

import io.runon.cryptocurrency.trading.Cryptocurrency;
import io.runon.cryptocurrency.trading.DataStream;
import io.runon.cryptocurrency.trading.SymbolCurrency;

/**
 * 실시간 캔들정보 얻기
 * https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-streams.md#websocket-limits
 * @author macle
 */
public abstract class BinanceCandleStream<T extends Cryptocurrency> extends DataStream<T> {

    
    

    private final String interval;

    /**
     * intervals
     *     //1m
     *     //3m
     *     //5m
     *     //15m
     *     //30m
     *     //1h
     *     //2h
     *     //4h
     *     //6h
     *     //8h
     *     //12h
     *     //1d
     *     //3d
     *     //1w
     *     //1M
     * @param streamId 아이디 (자유지정, 중복안됨)
     * @param interval intervals 참조
     */
    public BinanceCandleStream(String streamId, String interval) {
        super(streamId);
        this.interval = interval;
    }


    @Override
    public SymbolCurrency getSymbolCurrency(String cryptocurrencyId) {
        return BinanceSymbolCurrency.getSymbolCurrency(cryptocurrencyId);
    }

    @Override
    public void setSymbols(String symbols){

    }

    @Override
    public void setCurrencies(String Currencies){

    }



}
