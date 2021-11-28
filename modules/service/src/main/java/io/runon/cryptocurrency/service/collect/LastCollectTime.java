package io.runon.cryptocurrency.service.collect;

import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.time.Times;
import io.runon.cryptocurrency.service.redis.Redis;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        try {

            //눈으로 정보확인이 편하게 서울 시간을 넣고
            //구현할때는 시스템 시간을 활용한다.
            //check time 은 time + check 을 초과하면 에러로 알림을 주는 시간이다
            Instant i = Instant.ofEpochMilli(time);
            ZonedDateTime nowSeoul = ZonedDateTime.ofInstant(i , ZoneId.of("Asia/Seoul"));

            Redis.hsetAsync("collect_last_time", key,

                    nowSeoul.getYear() +"-" + nowSeoul.getMonthValue() + "-" + nowSeoul.getDayOfMonth() + " " + nowSeoul.getHour() + ":" + nowSeoul.getMinute() +":" + nowSeoul.getSecond()
                    + "," + time
                    + "," + checkTime
            );

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }


    public static void put(LastCollectTime [] lastCollectTimes){
        try {
            Map<String,String> map = new HashMap<>();

            for(LastCollectTime lastCollectTime : lastCollectTimes) {
                long time = Objects.requireNonNullElseGet(lastCollectTime.time, System::currentTimeMillis);
                long checkTime = Objects.requireNonNullElse(lastCollectTime.checkTime, Times.MINUTE_5);
                Instant i = Instant.ofEpochMilli(time);
                ZonedDateTime nowSeoul = ZonedDateTime.ofInstant(i, ZoneId.of("Asia/Seoul"));
//                commands.hset("collect_last_time", lastCollectTime.key,
//                        nowSeoul.getYear() + "-" + nowSeoul.getMonthValue() + "-" + nowSeoul.getDayOfMonth() + " " + nowSeoul.getHour() + ":" + nowSeoul.getMinute() + ":" + nowSeoul.getSecond()
//                                + "," + time
//                                + "," + checkTime
//                );
                map.put(lastCollectTime.key
                        , nowSeoul.getYear() + "-" + nowSeoul.getMonthValue() + "-" + nowSeoul.getDayOfMonth() + " " + nowSeoul.getHour() + ":" + nowSeoul.getMinute() + ":" + nowSeoul.getSecond()
                        + "," + time
                        + "," + checkTime);
            }
            Redis.hsetAsync("collect_last_time",map);

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }
}
