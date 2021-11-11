package io.runon.cryptocurrency.service.collect;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.time.Times;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.runon.cryptocurrency.service.CryptocurrencyRedis;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 마지막 수집주기
 * @author macle
 */
@Slf4j
@Data
public class LastCollectTime {

    private String key;
    private Long time;
    private Long checkTime;


    public static void put(String key){
        put(key, System.currentTimeMillis(), Times.MINUTE_5);
    }

    public static void put(String key, long checkTime){
        put(key, System.currentTimeMillis(), checkTime);
    }

    public static void put(String key, long time , long checkTime){

        try (StatefulRedisConnection<String, String> connection =  CryptocurrencyRedis.getRedisClient().connect()) {

            //눈으로 정보확인이 편하게 서울 시간을 넣고
            //구현할때는 시스템 시간을 활용한다.
            //check time 은 time + check 을 초과하면 에러로 알림을 주는 시간이다
            RedisAsyncCommands<String, String> commands = connection.async();
            Instant i = Instant.ofEpochMilli(time);
            ZonedDateTime nowSeoul = ZonedDateTime.ofInstant(i , ZoneId.of("Asia/Seoul"));

            commands.hset("collect_last_time", key,

                    nowSeoul.getYear() +"-" + nowSeoul.getMonthValue() + "-" + nowSeoul.getDayOfMonth() + " " + nowSeoul.getHour() + ":" + nowSeoul.getMinute() +":" + nowSeoul.getSecond()
                    + "," + time
                    + "," + checkTime
            );

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }


    public static void put(LastCollectTime [] lastCollectTimes){
        try (StatefulRedisConnection<String, String> connection =  CryptocurrencyRedis.getRedisClient().connect()) {

            for(LastCollectTime lastCollectTime : lastCollectTimes) {

                RedisAsyncCommands<String, String> commands = connection.async();
                commands.setAutoFlushCommands(true);
                long time;
                if(lastCollectTime.time == null){
                    time = System.currentTimeMillis();
                }else{
                    time = lastCollectTime.time;
                }

                long checkTime;
                if(lastCollectTime.checkTime == null){
                    checkTime = Times.MINUTE_5;
                }else{
                    checkTime = lastCollectTime.checkTime;
                }

                Instant i = Instant.ofEpochMilli(time);

                ZonedDateTime nowSeoul = ZonedDateTime.ofInstant(i, ZoneId.of("Asia/Seoul"));

                commands.hset("collect_last_time", lastCollectTime.key,
                        nowSeoul.getYear() + "-" + nowSeoul.getMonthValue() + "-" + nowSeoul.getDayOfMonth() + " " + nowSeoul.getHour() + ":" + nowSeoul.getMinute() + ":" + nowSeoul.getSecond()
                                + "," + time
                                + "," + checkTime
                );
            }

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }
}
