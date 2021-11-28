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
        //여기에 클러스터 or 클라이언트 어떤걸로 탈지 소스 넣기
        
        return new RedisConnectClient();
    }

}
