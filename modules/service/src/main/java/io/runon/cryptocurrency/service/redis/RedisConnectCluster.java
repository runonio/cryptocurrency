package io.runon.cryptocurrency.service.redis;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.async.RedisHashAsyncCommands;
import io.lettuce.core.api.async.RedisStringAsyncCommands;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

public class RedisConnectCluster implements RedisConnect{
    @Override
    public RedisStringCommands<String, String> syncString() {
        return null;
    }

    @Override
    public RedisHashCommands<String, String> syncHash() {
        return null;
    }

    @Override
    public RedisStringAsyncCommands<String, String> asyncString() {
        return null;
    }

    @Override
    public RedisHashAsyncCommands<String, String> asyncHash() {
        return null;
    }

    @Override
    public StatefulConnection<String, String> connection() {
        return null;
    }

    @Override
    public StatefulRedisPubSubConnection<String, String> connectPubSub() {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
