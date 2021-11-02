package io.runon.cryptocurrency.service;

import com.seomse.commons.config.Config;
import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 암호화폐 radis 공통
 * @author macle
 */
@Slf4j
public class CryptocurrencyRedis {

    private final static RedisClient REDIS_CLIENT = newClient();

    public static RedisClient newClient(){


        String host;
        String port;
        try {
            Map<String, Object> redisMap = CryptocurrencyYml.getYmlMap("redis");
            host = redisMap.get("host").toString();
            port = redisMap.get("port").toString();
        }catch(Exception e){
            log.debug(e.getMessage());
            log.debug("redis seomse config use");

            host = Config.getConfig("cryptocurrency.redis.host", "cryptocurrency.runon.io");
            port = Config.getConfig("cryptocurrency.redis.port", "16379");
        }

        return RedisClient.create("redis://" + host + ":" +port);
    }

    /**
     * 암호화폐 서비스에서 사용하는 redis client
     * @return redis client
     */
    public static RedisClient getRedisClient(){
        return REDIS_CLIENT;
    }

}
