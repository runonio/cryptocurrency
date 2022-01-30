package io.runon.cryptocurrency.trading;

import io.runon.cryptocurrency.trading.exception.AlreadyStreamException;

import java.util.HashMap;
import java.util.Map;

/**
 * 데이터 스트림 관리
 * @author macle
 */
public class DataStreamManager {
    private static class Singleton {
        private static final DataStreamManager instance = new DataStreamManager();
    }

    public static DataStreamManager getInstance(){
        return Singleton.instance;
    }

    private DataStreamManager(){

    }

    private final Object lock = new Object();

    private final Map<String, DataStream<?>> streamMap = new HashMap<>();

    private DataStream<?>[] streams = new DataStream[0];

    public void add(String streamId, DataStream<?> dataStream){
        synchronized (lock){
            if(streamMap.containsKey(streamId)){
                throw new AlreadyStreamException("stream id " + streamId);
            }
            streamMap.put(streamId, dataStream);
            streams = streamMap.values().toArray(new DataStream[0]);
        }
    }

    public boolean remove(String streamId){
        synchronized (lock){
            boolean isRemove = streamMap.remove(streamId) != null;
            if(isRemove){
                streams =  streamMap.values().toArray(new DataStream[0]);
            }

            return isRemove;
        }
    }

    @SuppressWarnings("rawtypes")
    public DataStream[] getStreams() {
        return streams;
    }

}
