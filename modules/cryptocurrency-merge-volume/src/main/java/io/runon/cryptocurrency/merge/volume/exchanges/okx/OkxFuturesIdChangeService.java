package io.runon.cryptocurrency.merge.volume.exchanges.okx;

import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtil;
import io.runon.commons.utils.string.Strings;
import io.runon.commons.utils.time.Times;
import io.runon.cryptocurrency.exchanges.okx.OkxExchange;
import lombok.extern.slf4j.Slf4j;

/**
 * Okx 선물거래소의 종목 아이디 변경이 되면 다시연결 처리
 * @author macle
 */
@Slf4j
public class OkxFuturesIdChangeService extends Service {

    private final OkxFuturesBtcTradeStream stream;

    public OkxFuturesIdChangeService(OkxFuturesBtcTradeStream stream){
        setServiceId(this.getClass().getName());
        setDelayStartTime(Times.MINUTE_5);
        setSleepTime(Times.MINUTE_5);
        setState(State.START);
        this.stream = stream;
    }

    @Override
    public void work() {
        try{
            String [] ids = OkxExchange.getIds("BTC","FUTURES");
            if(ids.length == 0){
                return;
            }

            if(Strings.equalsSort(stream.ids, ids)){
                return;
            }

            //아이디가 바뀐경우
            //재 연결한다
            stream.close();

            stream.ids = ids;
            stream.setSubscribeMessage(OkxExchange.getTradeSubscribeMessage(ids));

            stream.connect();

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }
    }
}
