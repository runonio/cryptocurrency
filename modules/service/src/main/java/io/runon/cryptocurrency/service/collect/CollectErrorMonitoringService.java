package io.runon.cryptocurrency.service.collect;

import com.seomse.commons.service.Service;
import com.seomse.commons.utils.ExceptionUtil;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.runon.cryptocurrency.service.CryptocurrencyRedis;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * 수집에러 모니터링 서비스
 * @author macle
 */
@Slf4j
public class CollectErrorMonitoringService extends Service {

    public CollectErrorMonitoringService(){
        super();
        setDelayStartTime(10000L);
        //30초에 한번씩 반복
        setSleepTime(1000L*30L);
        setState(State.START);
    }

    @Override
    public void work() {

        try (StatefulRedisConnection<String, String> connection =  CryptocurrencyRedis.getRedisClient().connect()) {
            RedisAsyncCommands<String, String> commands = connection.async();
            commands.setAutoFlushCommands(true);

            Map<String, String> hgetall = commands.hgetall("collect_last_time").get();

            Set<String> keys = hgetall.keySet();

            long time = System.currentTimeMillis();
            
            for(String key: keys){
                String message = hgetall.get(key);

                String [] values = message.split(",");
                
                long overTime = Long.parseLong(values[1]) + Long.parseLong(values[2]);
                
                if(time > overTime){
                    //에러떨구기
                    log.error("collect error: " +key + ", last date: " + values[0] + " time: " + values[1] );
                }
                
                
            }

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }

    }
}
