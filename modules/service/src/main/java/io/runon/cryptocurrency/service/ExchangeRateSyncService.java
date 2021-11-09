package io.runon.cryptocurrency.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.seomse.commons.config.Config;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.ExceptionUtil;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 환율 동기화 서비스
 * @author macle
 */
@Slf4j
public class ExchangeRateSyncService extends Service {


    public ExchangeRateSyncService(){
        setServiceId(this.getClass().getName());

        setSleepTime(Config.getLong("cryptocurrency.service.exchange.rate.update.time" , 1000L));
        setState(State.START);
    }

    private final Gson gson = new Gson();

    @Override
    public void work() {
        try{
            //원달러 환율 가져오기
            BigDecimal usdDivideKrw = null;

            try(StatefulRedisConnection<String, String> connection = CryptocurrencyRedis.getRedisClient().connect()){
                String exchangeJson = connection.sync().get("exchange_rate");
                JsonObject JsonObject = gson.fromJson(exchangeJson, JsonObject.class);
                usdDivideKrw = JsonObject.get("price").getAsBigDecimal();
            }catch (Exception e){
                log.error(ExceptionUtil.getStackTrace(e));
            }

            if(usdDivideKrw == null){
                usdDivideKrw = new BigDecimal(1100);
            }
            ExchangeRateManager.getInstance().usdDivideKrw = usdDivideKrw;

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }
}
