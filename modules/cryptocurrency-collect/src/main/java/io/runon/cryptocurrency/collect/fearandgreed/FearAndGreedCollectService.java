package io.runon.cryptocurrency.collect.fearandgreed;

import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtils;
import io.runon.commons.utils.time.Times;
import lombok.extern.slf4j.Slf4j;

/**
 * @author macle
 */
@Slf4j
public class FearAndGreedCollectService extends Service {

    public FearAndGreedCollectService(){
        setDelayStartTime(Times.MINUTE_10);
        setSleepTime(Times.HOUR_2);
        setState(Service.State.START);
    }

    @Override
    public void work() {
        try{
            log.info("update fear and greed");
            FearAndGreedAlternativeCollect.update(100);
        }catch (Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
}
