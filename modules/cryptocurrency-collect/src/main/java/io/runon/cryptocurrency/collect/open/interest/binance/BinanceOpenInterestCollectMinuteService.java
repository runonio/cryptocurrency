package io.runon.cryptocurrency.collect.open.interest.binance;

import io.runon.commons.service.Service;
import io.runon.commons.utils.time.Times;
import lombok.extern.slf4j.Slf4j;

/**
 * 바이낸스 미 결제 약정
 * 분 단위 서비스
 * @author macle
 */
@Slf4j
public class BinanceOpenInterestCollectMinuteService extends Service {

    private final BinanceOpenInterestCollect collect;

    public BinanceOpenInterestCollectMinuteService(BinanceOpenInterestCollect collect){
        setSleepTime(Times.MINUTE_1);
        setState(State.START);
        this.collect = collect;
    }
    @Override
    public void work() {

        collect.collectOpenInterest();

        try{Thread.sleep(Times.MINUTE_1);}catch(Exception ignore){}

        collect.collectLongShortRatio();

        log.debug("collect openInterest minute");
    }
}
