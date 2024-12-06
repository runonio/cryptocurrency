package io.runon.cryptocurrency.trading.service;

import io.runon.commons.config.Config;
import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtil;
import io.runon.commons.utils.time.Times;
import io.runon.commons.data.service.collect.LastCollectTime;
import io.runon.cryptocurrency.trading.DataStream;
import io.runon.cryptocurrency.trading.DataStreamManager;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * DataStream 연결 유지 서비스
 * data stream keep-alive service
 * @author macle
 */
@Slf4j
public class DataStreamKeepAliveService extends Service {

    public DataStreamKeepAliveService(){
        setDelayStartTime(Config.getLong("data.stream.keep.alive.delay.start.time", Times.MINUTE_1));
        setSleepTime(Config.getLong("data.stream.keep.alive.sleep.time", 2000L));
        setState(State.START);
    }

    private final Map<String, Long> connectMap = new HashMap<>();
    
    @SuppressWarnings("rawtypes")
    @Override
    public void work() {

        try {
            //10초
            long reconnectTime = System.currentTimeMillis() - Config.getLong("data.stream.keep.alive.reconnect.time", 30000L);
            //3분
            long errorTime = System.currentTimeMillis() - Config.getLong("data.stream.keep.alive.reconnect.time", Times.MINUTE_3);

            DataStreamManager dataStreamManager = DataStreamManager.getInstance();
            DataStream[] streams = dataStreamManager.getStreams();
            if(streams.length == 0){
                return;
            }

            LastCollectTime [] times = new LastCollectTime[streams.length];
            for (int i = 0; i < streams.length ; i++) {
                DataStream stream = streams[i];
                
                if (reconnectTime > stream.getLastTime()) {
                    Long time = connectMap.get(stream.getStreamId());
                    //3분동안 재연결하지 않음
                    if(time == null || System.currentTimeMillis() - Times.MINUTE_3 > time) {
                        try {
                            stream.connect();
                        } catch (Exception e) {
                            log.error(ExceptionUtil.getStackTrace(e));
                        }
                        connectMap.put(stream.getStreamId(), System.currentTimeMillis());
                    }
                }
                times[i] = new LastCollectTime();
                times[i].setKey(stream.getStreamId());
                times[i].setTime(stream.getLastTime());
                times[i].setCheckTime(errorTime);
            }
            LastCollectTime.put(times);

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }
}
