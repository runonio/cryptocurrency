package io.runon.cryptocurrency.service.redis;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.async.RedisHashAsyncCommands;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.util.Map;
/**
 * @author macle
 */
public class RedisConnectCluster implements RedisConnect{

    private final RedisClusterClient redisClusterClient;
    private StatefulRedisClusterConnection<String, String> connection;
    RedisConnectCluster(Map<String, Object> redisMap){
        redisClusterClient= RedisClusterClient.create("redis://" + redisMap.get("host") + ":" + redisMap.get("port"));
    }

    private RedisAdvancedClusterAsyncCommands<String, String> async;
    private RedisAdvancedClusterCommands<String, String> sync;
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
        connection = redisClusterClient.connect();
        connection.setAutoFlushCommands(true);
        async = connection.async();
        sync = connection.sync();
        return connection;
    }

    @Override
    public StatefulRedisPubSubConnection<String, String> connectPubSub() {
        return redisClusterClient.connectPubSub();
    }

    @Override
    public void close() {
        try{
            connection.close();
        }catch(Exception ignore){}
        try{
            connection.closeAsync();
        }catch(Exception ignore){}
    }
}
