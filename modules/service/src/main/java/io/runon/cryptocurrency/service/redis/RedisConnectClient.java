package io.runon.cryptocurrency.service.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.async.RedisHashAsyncCommands;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author macle
 */
@Slf4j
public class RedisConnectClient implements RedisConnect{

    private final RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;

    RedisConnectClient(Map<String, Object> redisMap){
        redisClient =  RedisClient.create("redis://" + redisMap.get("host") + ":" + redisMap.get("port"));
    }



    private RedisAsyncCommands<String, String> async;
    private RedisCommands<String , String > sync;
    @Override
    public RedisStringCommands<String, String> syncString() {
        return sync;
    }

    @Override
    public RedisHashCommands<String, String> syncHash() {
        return sync;
    }

    @Override
    public RedisStringAsyncCommands<String, String> asyncString() {
        return async;
    }

    @Override
    public RedisHashAsyncCommands<String, String> asyncHash() {
        return async;
    }

    @Override
    public StatefulConnection<String, String> connection() {
        connection = redisClient.connect();
        connection.setAutoFlushCommands(true);
        async = connection.async();
        sync = connection.sync();
        return connection;
    }

    @Override
    public StatefulRedisPubSubConnection<String, String> connectPubSub() {
        return redisClient.connectPubSub();
    }

    @Override
    public void close(){
        try{
            connection.close();
        }catch(Exception ignore){}
        try{
            connection.closeAsync();
        }catch(Exception ignore){}
    }
}
