package io.runon.cryptocurrency.merge.volume;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.runon.commons.callback.ObjCallback;
import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtil;
import io.runon.commons.utils.time.Times;
import io.runon.trading.BigDecimals;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.file.TimeName;
import io.runon.trading.technical.analysis.candle.TradeCandle;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * 5초마다 가격 거래량 거래대금 ( 체결강도 거래량 거래대금 저장)
 * @author macle
 */
@Slf4j
public class MergeVolumeService extends Service {

    public static final ZoneId ZONE_ID = TradingTimes.UTC_ZONE_ID;

    public static final TimeName.Type TIME_NAME_TYPE = TimeName.Type.DAY_5;

    private final MergeVolume mergeVolume;

    private ObjCallback objCallback;

    private BigDecimal lastPrice;
    private BigDecimal lastPriceFutures;

    public MergeVolumeService(MergeVolume mergeVolume){
        setServiceId(this.getClass().getName());
        this.mergeVolume = mergeVolume;
        setDelayStartTime(7000L);
        setSleepTime(1000L);
        setState(State.START);

        lastPrice = mergeVolume.price;
        lastPriceFutures = mergeVolume.priceFutures;

        new MergeVolumeRecordService(this).start();
    }

    private final Gson gson = new Gson();

    public void setObjCallback(ObjCallback objCallback) {
        this.objCallback = objCallback;
    }

    boolean isRecord = true;

    public void setRecord(boolean record) {
        isRecord = record;
    }

    private boolean isConsoleOut = true;

    public void setConsoleOut(boolean consoleOut) {
        isConsoleOut = consoleOut;
    }

    final List<String> lineList = new ArrayList<>();

    final Object lock = new Object();

    @Override
    public void work() {
        try{
            long startTime = System.currentTimeMillis();

            VolumePriceData volumeData = mergeVolume.getVolumeData();
            if(volumeData == null || lastPrice == null || lastPriceFutures == null){
                lastPrice = mergeVolume.price;
                lastPriceFutures = mergeVolume.priceFutures;
                Thread.sleep(4000);
                return;
            }

            if(volumeData.price == null){
                lastPrice = mergeVolume.price;
                lastPriceFutures = mergeVolume.priceFutures;
                Thread.sleep(4000);
                return;
            }

            long time = System.currentTimeMillis();

            JsonObject object = new JsonObject();
            object.addProperty("t", time);

            BigDecimal price = mergeVolume.price;
            BigDecimal priceFutures = mergeVolume.priceFutures;
            object.addProperty("p", price.stripTrailingZeros().toPlainString());
            object.addProperty("pf", priceFutures.stripTrailingZeros().toPlainString());
            object.addProperty("p_cr", price.subtract(lastPrice).divide(lastPrice,8, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());
            object.addProperty("pf_cr", priceFutures.subtract(lastPriceFutures).divide(lastPriceFutures,8, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());


            lastPrice = price;
            lastPriceFutures = priceFutures;

            //평균 거래량은 다른방식으로 구한다.
            object.addProperty("avg_5s", BigDecimals.getText(mergeVolume.avg5s,6));
            object.addProperty("avg_1m", BigDecimals.getText(mergeVolume.avg1m,6));


            JsonArray array = new JsonArray();
            array.add(BigDecimals.getText(volumeData.volume,6));
            array.add(BigDecimals.getText(volumeData.amount,2));
            array.add(BigDecimals.getText(volumeData.getVolumePower(),2));
            object.add("5s", array);

            long [] secondTimes = mergeVolume.getSecondTimes();
            for(long second : secondTimes){
                TradeCandle tradeCandle = mergeVolume.getSecondLastCandle(second);

                if(tradeCandle == null){
                    continue;
                }

                if(tradeCandle.getClose() == null){
                    continue;
                }
                array = new JsonArray();

                array.add(BigDecimals.getText(tradeCandle.getVolume(),6));
                array.add(BigDecimals.getText(tradeCandle.getAmount(),2));
                array.add(BigDecimals.getText(tradeCandle.getVolumePower(),2));

                if(second < Times.MINUTE_1){
                    object.add((second/1000) + "s", array);
                }else{
                    object.add((second/Times.MINUTE_1) + "m", array);
                }
            }

            String volumeJson = gson.toJson(object);

            if(isConsoleOut) {
                System.out.println(Times.ymdhm(time, ZONE_ID) + " " + volumeJson);
            }

            if(isRecord) {
                synchronized (lock){
                    lineList.add(volumeJson);
                }
            }

            if(objCallback != null){
                objCallback.callback(volumeJson);
            }

            long sleep = 4000 - (System.currentTimeMillis() - startTime);
            if(sleep > 100){
                Thread.sleep(sleep);
            }

        }catch (Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
            try{Thread.sleep(4000);}catch (Exception ignore){}
        }
    }


}
