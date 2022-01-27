package io.runon.cryptocurrency.trading;

import io.runon.trading.Trade;
import io.runon.trading.technical.analysis.candle.TradeCandle;

import java.util.HashMap;
import java.util.Map;

/**
 * 캔들 혹은 거래데이터 받기 (리얼타임)
 * @author macle
 */
public abstract class DataStream<T extends Cryptocurrency> {

    protected final Object lock = new Object();
    protected final String streamId;

    public DataStream(String streamId){
        this.streamId = streamId;
        DataStreamManager.getInstance().add(streamId, this);
    }




    protected final Map<String, T> cryptocurrencyMap = new HashMap<>();
    protected long lastTime = 0L;

    public void addCandle(String cryptocurrencyId, TradeCandle tradeCandle){
        lastTime = System.currentTimeMillis();
        T cryptocurrency = getOrNewCryptocurrency(cryptocurrencyId);
        cryptocurrency.addCandle(tradeCandle);
    }

    public void addTrade(String cryptocurrencyId, Trade trade){
        lastTime = System.currentTimeMillis();
        T cryptocurrency = getOrNewCryptocurrency(cryptocurrencyId);
        cryptocurrency.addTrade(trade);
    }

    private T getOrNewCryptocurrency(String cryptocurrencyId){
        T cryptocurrency = cryptocurrencyMap.get(cryptocurrencyId);
        if(cryptocurrency == null){
            synchronized (lock){
                //동기화 구간에서 다시 체크
                cryptocurrency = cryptocurrencyMap.get(cryptocurrencyId);
                if(cryptocurrency == null){
                    cryptocurrency = newCryptocurrency(cryptocurrencyId);
                    cryptocurrencyMap.put(cryptocurrencyId, cryptocurrency);
                }
            }
        }
        return cryptocurrency;
    }

    public Cryptocurrency [] getCryptocurrencies(){
        synchronized (lock){
            return cryptocurrencyMap.values().toArray(new Cryptocurrency[0]);
        }
    }

    public boolean removeCryptocurrency(String cryptocurrencyId){
        synchronized (lock){
            return cryptocurrencyMap.remove(cryptocurrencyId) != null;
        }
    }

    public T getCryptocurrency(String cryptocurrencyId){
        return cryptocurrencyMap.get(cryptocurrencyId);
    }

    public String getStreamId() {
        return streamId;
    }

    public abstract T newCryptocurrency(String cryptocurrencyId);
    public abstract SymbolCurrency getSymbolCurrency(String cryptocurrencyId);
    public abstract void setSymbols(String symbols);
    public abstract void setCurrencies(String Currencies);

    public long getLastTime() {
        return lastTime;
    }

}
