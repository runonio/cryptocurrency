package io.runon.cryptocurrency.trading.service;

import com.seomse.commons.config.Config;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.FileUtil;
import io.runon.cryptocurrency.trading.Cryptocurrency;
import io.runon.cryptocurrency.trading.CryptocurrencyLastCandle;
import io.runon.cryptocurrency.trading.DataStreamCandle;
import io.runon.trading.data.csv.CsvCandle;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 실시간 캔들 추출 서비스
 * 백테스팅용
 * @author macle
 */
public class CandleOutRealTimeService extends Service {

    private final DataStreamCandle<CryptocurrencyLastCandle> dataStream;

    private final String outDir;

    private ZoneId zoneId = ZoneId.of("Asia/Seoul");


    public CandleOutRealTimeService(DataStreamCandle<CryptocurrencyLastCandle> dataStream){

        setSleepTime(Config.getLong("candle.out.realtime.cycle", 1000L));
        setState(State.START);

        this.dataStream = dataStream;
        outDir = Config.getConfig("candle.out.path","data/candle/realtime") + "/";
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    private final Map<String, Long> timeMap = new HashMap<>();

    @Override
    public void work() {

        long time = System.currentTimeMillis();

        Cryptocurrency[] cryptocurrencies = dataStream.getCryptocurrencies();
        for(Cryptocurrency cryptocurrency : cryptocurrencies){

            CryptocurrencyLastCandle cryptocurrencyLastCandle = (CryptocurrencyLastCandle) cryptocurrency;
            if(cryptocurrencyLastCandle.getLastCandle() == null){
                continue;
            }

            String path = outDir + cryptocurrency.getId();
            Long lastTime = timeMap.get(cryptocurrency.getId());
            if(lastTime == null){
//                FileUtil.fileOutput(CsvCandle.value(candle));

                continue;
            }

        }


    }
}
