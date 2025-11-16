package io.runon.cryptocurrency.collect.starter;

import io.runon.commons.config.JsonFileProperties;
import io.runon.commons.config.JsonFilePropertiesManager;
import io.runon.commons.apis.http.HttpApiResponse;
import io.runon.commons.apis.http.HttpApis;
import io.runon.commons.service.Service;
import io.runon.commons.utils.ExceptionUtils;
import io.runon.commons.utils.time.Times;
import io.runon.cryptocurrency.merge.volume.MergeVolumeService;
import io.runon.cryptocurrency.collect.open.interest.binance.BinanceOpenInterestCollect;
import io.runon.cryptocurrency.trading.CryptocurrencyDataPath;
import io.runon.trading.TradingConfig;
import io.runon.trading.data.TradingDataPath;
import io.runon.trading.data.file.LineOutManager;
import io.runon.trading.data.file.PathTimeLine;
import io.runon.trading.data.file.TimeLineLock;
import io.runon.trading.data.json.JsonOrgUtils;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

/**
 * 런온 중앙서버로 데이터를 전송한다
 * @author macle
 */
@SuppressWarnings("BusyWait")
@Slf4j
public class CryptocurrencyDataUploadService extends Service {


    private final JsonFileProperties jsonFileProperties;

    private final BinanceOpenInterestCollect openInterest;

    private final LineOutManager lineOutManager = LineOutManager.getInstance();

    private final long sendSleep = 1000L;

    public CryptocurrencyDataUploadService(BinanceOpenInterestCollect openInterest){
        setDelayStartTime(Times.MINUTE_1);
        setSleepTime(Times.MINUTE_5);
        setState(State.START);
        this.openInterest = openInterest;
        jsonFileProperties = JsonFilePropertiesManager.getInstance().getByName("cryptocurrency_data_collect.json");
    }

    int count = 2000;

    @Override
    public void work() {
        try{
            //미체결 약정
            sendOpenInterest();
        }catch (Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }

        try{
            //볼륨 합산정보
            sendVolumeMerge();
        }catch (Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }


    public void sendOpenInterest(){
        String [] paths = openInterest.getPaths();
        //미체결약정
        for(String path : paths ) {

            TimeLineLock timeLineLock = lineOutManager.get(TradingDataPath.getAbsolutePath(path), PathTimeLine.CSV,  BinanceOpenInterestCollect.TIME_NAME_TYPE);
            long lastTime = jsonFileProperties.getLong(path, 0L);
            for (; ; ) {

                String[] lines = timeLineLock.load(lastTime + 1, count);
                if (lines.length == 0) {
                    break;
                }

                //데이터 전송 로직
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lines", JsonOrgUtils.getArray(lines));
                jsonObject.put("line_time_term", 2000L);
                jsonObject.put("zone_id", BinanceOpenInterestCollect.ZONE_ID.toString());
                jsonObject.put("dir_path", path);
                jsonObject.put("time_name_type", BinanceOpenInterestCollect.TIME_NAME_TYPE.toString());

                try{
                    Thread.sleep(sendSleep);
                }catch (Exception ignore){}

                log.debug("send lines: " + path +", " + Times.ymdhm(PathTimeLine.CSV.getTime(lines[0]), BinanceOpenInterestCollect.ZONE_ID));

                HttpApiResponse response = HttpApis.postJson(TradingConfig.RUNON_API_ADDRESS +"/api/time/data/updatesum/lines", jsonObject.toString());
                if(response.getResponseCode() != 200){
                    log.error(response.getResponseCode() + ", " + response.getMessage());
                    break;
                }

                String message = response.getMessage();
                JSONObject responseMessage = new JSONObject(message);
                String code = responseMessage.getString("code");
                if(!code.equals("1")){
                    log.error(code + ", " + responseMessage.getString("message"));
                    break;
                }

                lastTime = PathTimeLine.CSV.getTime(lines[lines.length - 1]);

                //전송 시간기록
                jsonFileProperties.set(path, lastTime);
            }

        }
    }

    public void sendVolumeMerge(){

        String relativePath = TradingDataPath.getRelativePath( CryptocurrencyDataPath.getMergeVolumeDirPath());
        String absolutePath = TradingDataPath.getAbsolutePath(relativePath);

        long lastTime = jsonFileProperties.getLong(relativePath, 0L);
        for(;;) {
            TimeLineLock timeLineLock = lineOutManager.get(absolutePath, PathTimeLine.JSON,  MergeVolumeService.TIME_NAME_TYPE);
            String[] lines = timeLineLock.load(lastTime + 1, count);

            if(lines.length == 0){
                break;
            }
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("lines", JsonOrgUtils.getArray(lines));
            jsonObject.put("line_time_term", 2000L);
            jsonObject.put("zone_id", MergeVolumeService.ZONE_ID.toString());
            jsonObject.put("dir_path", relativePath);
            jsonObject.put("time_name_type", MergeVolumeService.TIME_NAME_TYPE.toString());

            try{
                Thread.sleep(sendSleep);
            }catch (Exception ignore){}

            log.debug("send lines: " + relativePath +", " + Times.ymdhm(PathTimeLine.JSON.getTime(lines[0]), BinanceOpenInterestCollect.ZONE_ID));

            HttpApiResponse response = HttpApis.postJson(TradingConfig.RUNON_API_ADDRESS + "/api/time/data/updatesum/lines", jsonObject.toString());
            if (response.getResponseCode() != 200) {
                log.error(response.getResponseCode() + ", " + response.getMessage());
                break;
            }

            String message = response.getMessage();
            JSONObject responseMessage = new JSONObject(message);
            String code = responseMessage.getString("code");
            if(!code.equals("1")){
                log.error(code + ", " + responseMessage.getString("message"));
                break;
            }

            lastTime = PathTimeLine.JSON.getTime(lines[lines.length - 1]);

            jsonFileProperties.set(relativePath, lastTime);
        }
    }
    public static void main(String[] args) {


        String relativePath = TradingDataPath.getRelativePath( CryptocurrencyDataPath.getMergeVolumeDirPath());
        String absolutePath = TradingDataPath.getAbsolutePath(relativePath);

        System.out.println(relativePath + ", " + absolutePath);

    }
}
