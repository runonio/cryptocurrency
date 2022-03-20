package io.runon.cryptocurrency.trading.service;

import com.seomse.commons.config.Config;
import com.seomse.commons.service.Service;
import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.time.DateUtil;
import io.runon.cryptocurrency.trading.Cryptocurrency;
import io.runon.cryptocurrency.trading.CryptocurrencyLastCandle;
import io.runon.cryptocurrency.trading.DataStreamCandle;
import io.runon.trading.data.csv.CsvCandle;
import io.runon.trading.technical.analysis.candle.TradeCandle;

import java.io.File;
import java.time.Instant;
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
        outDir = Config.getConfig("candle.out.path","data/candle/realtime") + "/"  + dataStream.getStreamId() + "/";
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    private final Map<String, Long> timeMap = new HashMap<>();


    @Override
    public void work() {

        Cryptocurrency[] cryptocurrencies = dataStream.getCryptocurrencies();
        for(Cryptocurrency cryptocurrency : cryptocurrencies){

            CryptocurrencyLastCandle cryptocurrencyLastCandle = (CryptocurrencyLastCandle) cryptocurrency;

            TradeCandle previousCandle = cryptocurrencyLastCandle.getPreviousCandle();
            long previousTime = cryptocurrencyLastCandle.getPreviousTime();

            TradeCandle tradeCandle = cryptocurrencyLastCandle.getLastCandle();
            long candleLastTime =  cryptocurrencyLastCandle.getLastTime();

            if(tradeCandle == null){
                continue;
            }

            Instant intent = Instant.ofEpochMilli(candleLastTime);
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(intent , zoneId);

            String dirPath = outDir  + cryptocurrency.getId();
            File dir = new File(dirPath);
            if(!dir.isDirectory()){
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }

            String path = dirPath + "/"  + zonedDateTime.getYear()+ DateUtil.getDateText(zonedDateTime.getMonthValue())
                    +  DateUtil.getDateText(zonedDateTime.getDayOfMonth()) + DateUtil.getDateText(zonedDateTime.getHour());

            Long lastTime = timeMap.get(cryptocurrency.getId());
            if(lastTime == null){
                FileUtil.fileOutput(candleLastTime + "," + CsvCandle.value(tradeCandle)+"\n", path, true);
                timeMap.put(cryptocurrency.getId(), candleLastTime);
                continue;
            }

            //캔들이 변한게 없을경우
            if(candleLastTime == lastTime){
                continue;
            }

            if(previousCandle != null && previousTime > lastTime){
                FileUtil.fileOutput(previousTime + "," + CsvCandle.value(previousCandle)+"\n", path, true);
            }

            FileUtil.fileOutput(candleLastTime + "," + CsvCandle.value(tradeCandle)+"\n", path, true);

            timeMap.put(cryptocurrency.getId(), candleLastTime);

        }


    }
}
