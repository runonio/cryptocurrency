package io.runon.cryptocurrency.service.redis;

import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.async.RedisHashAsyncCommands;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author macle
 */
@Slf4j
public class ServiceRedis {


    final static ServiceRedis instance = new ServiceRedis();

    public static ServiceRedis getInstance() {
        return instance;
    }

    private final RedisConnect redisConnect;

    private StatefulConnection<String, String> connection;
    private RedisHashAsyncCommands<String, String> asyncHash;
    private RedisStringAsyncCommands<String, String> asyncString;
    private RedisStringCommands<String, String> syncString;
    private RedisHashCommands<String, String> syncHash;

    private StatefulRedisPubSubConnection<String, String> pubConnection;
    private RedisPubSubAsyncCommands<String, String> pubCommands;

    private ServiceRedis(){
        redisConnect = RedisConnectFactory.newRedisConnect();

        connection = redisConnect.connection();
        asyncHash = redisConnect.asyncHash();
        asyncString = redisConnect.asyncString();
        syncString = redisConnect.syncString();
        syncHash = redisConnect.syncHash();

        pubConnection = redisConnect.connectPubSub();
        pubConnection.setAutoFlushCommands(true);
        pubCommands = pubConnection.async();

    }


    private final Object lock = new Object();

    private final Object lockPubSub = new Object();

    public RedisFuture<Long> publish(String channel, String message){
        synchronized (lockPubSub){
            connectPubSub();
            return pubCommands.publish(channel, message);
        }
    }

    public Map<String, String> hgetall(String key){
        synchronized (lock){
            connect();
            return syncHash.hgetall(key);
        }
    }
    public Map<String, String> hgetallAsync(String key){
        synchronized (lock){
            connect();
            try {
                //받는쪽에서 처리
                return asyncHash.hgetall(key).get();
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    public String hget(String key, String field) {
        synchronized (lock){
            connect();
            return syncHash.hget(key, field);
        }
    }

    public RedisFuture<Boolean> hsetAsync(String key, String field, String value){
        synchronized (lock){
            connect();
            return asyncHash.hset(key,field,value);
        }

    }

    public RedisFuture<Long> hsetAsync(String key, Map<String, String> map){
        synchronized (lock){
            connect();
            return asyncHash.hset(key,map);
        }
    }

    public RedisFuture<Long> hdelAsync(String key,  String... fields){
        synchronized (lock){
            connect();
            return asyncHash.hdel(key, fields);
        }

    }

    public RedisFuture<String> setAsync(String key, String value){
        synchronized (lock){
            connect();
            return asyncString.set(key, value);
        }
    }

    public String getAsync(String key) {
        synchronized (lock) {
            connect();
            try {

                RedisFuture<String> value = asyncString.get(key);
                if(value == null){
                    return null;
                }
                return value.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String get(String key){
        synchronized (lock){
            connect();
            return syncString.get(key);
        }
    }


    private void connect(){
        if (!connection.isOpen()) {
            connection.close();
            connection = redisConnect.connection();
            asyncHash = redisConnect.asyncHash();
            asyncString = redisConnect.asyncString();
            syncString = redisConnect.syncString();
            syncHash = redisConnect.syncHash();
        }
    }

    private void connectPubSub(){
        if (!pubConnection.isOpen()) {
            pubConnection = redisConnect.connectPubSub();
            pubConnection.setAutoFlushCommands(true);
            pubCommands = pubConnection.async();
        }
    }

}
