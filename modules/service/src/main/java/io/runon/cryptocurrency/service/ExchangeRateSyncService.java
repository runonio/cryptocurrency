package io.runon.cryptocurrency.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.seomse.commons.config.Config;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.ExceptionUtil;
import io.runon.cryptocurrency.service.redis.Redis;
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

            String exchangeJson = Redis.get("exchange_rate");
            JsonObject JsonObject = gson.fromJson(exchangeJson, JsonObject.class);
            BigDecimal usdDivideKrw = JsonObject.get("price").getAsBigDecimal();


            if(usdDivideKrw == null){
                usdDivideKrw = new BigDecimal(1100);
            }
            ExchangeRateManager.getInstance().usdDivideKrw = usdDivideKrw;

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }
}
