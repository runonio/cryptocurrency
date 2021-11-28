package io.runon.cryptocurrency.service.redis;

import io.lettuce.core.RedisFuture;

import java.util.Map;

/**
 * @author macle
 */
@SuppressWarnings("UnusedReturnValue")
public class Redis {

    public static RedisFuture<Long> publish(String channel, String message){
        return ServiceRedis.instance.publish(channel,message);
    }

    public static Map<String, String> hgetall(String key){
        return ServiceRedis.instance.hgetall(key);
    }

    public static Map<String, String> hgetallAsync(String key){
        return ServiceRedis.instance.hgetallAsync(key);
    }

    public static String get(String key){
        return ServiceRedis.instance.get(key);
    }


    public static RedisFuture<Boolean> hsetAsync(String key, String field, String value){
        return ServiceRedis.instance.hsetAsync(key,field,value);
    }

    public static RedisFuture<Long> hsetAsync(String key, Map<String, String> map){
        return ServiceRedis.instance.hsetAsync(key, map);
    }
}