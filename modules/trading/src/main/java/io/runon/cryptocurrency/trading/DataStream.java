package io.runon.cryptocurrency.trading;

import java.util.HashMap;
import java.util.Map;

/**
 * 캔들 혹은 거래데이터 받기 (리얼타임)
 * @author macle
 */
public abstract class DataStream<T extends Cryptocurrency> implements AutoCloseable{

    protected final Object lock = new Object();
    protected final String streamId;

    public DataStream(String streamId){
        this.streamId = streamId;
        DataStreamManager.getInstance().add(streamId, this);
    }

    protected final Map<String, T> cryptocurrencyMap = new HashMap<>();
    protected long lastTime = 0L;

    protected T computeIfAbsent(String cryptocurrencyId){

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
    public abstract MarketSymbol getMarketSymbol(String cryptocurrencyId);
    public abstract void connect();

    public long getLastTime() {
        return lastTime;
    }

}
