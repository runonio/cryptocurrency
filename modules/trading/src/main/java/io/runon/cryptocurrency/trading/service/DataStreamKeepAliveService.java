package io.runon.cryptocurrency.trading.service;

import com.seomse.commons.config.Config;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.time.Times;
import io.runon.commons.data.service.collect.LastCollectTime;
import io.runon.cryptocurrency.trading.DataStream;
import io.runon.cryptocurrency.trading.DataStreamManager;
import lombok.extern.slf4j.Slf4j;

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

    @SuppressWarnings("rawtypes")
    @Override
    public void work() {

        try {
            //30초
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
                    try{stream.connect();}catch(Exception e){log.error(ExceptionUtil.getStackTrace(e));}
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
