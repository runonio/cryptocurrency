package io.runon.cryptocurrency.service.redis;
/**
 * RedisClient, RedisClusterClient 를 활용한  RedisConnect 생성
 * @author macle
 */
public class RedisConnectFactory {

    /**
     * 설정파일을 읽어서 RedisConnect 정보생성
     * @return RedisConnect
     */
    public static RedisConnect newRedisConnect(){
        return new RedisConnectClient();
    }

}
