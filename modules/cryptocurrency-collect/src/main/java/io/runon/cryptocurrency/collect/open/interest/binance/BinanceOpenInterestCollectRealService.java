package io.runon.cryptocurrency.collect.open.interest.binance;

import io.runon.commons.service.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * 바이낸스 분 단위 서비스
 * @author macle
 */
@Slf4j
public class BinanceOpenInterestCollectRealService extends Service {

    private final BinanceOpenInterestCollect collect;

    public BinanceOpenInterestCollectRealService(BinanceOpenInterestCollect collect){
        setSleepTime(1000L*30);
        setState(State.START);
        this.collect = collect;
    }

    @Override
    public void work() {
        collect.collectOpenInterestReal();
        log.debug("collect openInterest real");
    }
}
