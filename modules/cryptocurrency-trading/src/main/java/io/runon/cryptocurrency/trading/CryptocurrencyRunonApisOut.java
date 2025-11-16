package io.runon.cryptocurrency.trading;

import io.runon.commons.config.JsonFileProperties;
import io.runon.commons.apis.http.HttpApiResponse;
import io.runon.commons.apis.http.HttpApis;
import io.runon.commons.utils.ExceptionUtils;
import io.runon.trading.TradingConfig;
import io.runon.trading.TradingTimes;
import io.runon.trading.data.TradingDataPath;
import io.runon.trading.data.file.*;
import io.runon.trading.data.json.JsonOrgUtils;
import io.runon.trading.exception.TradingApiException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 런온 api를 활용하여 암호화폐데이터 내리기
 * @author macle
 */
@Slf4j
public class CryptocurrencyRunonApisOut {
    private final JsonFileProperties jsonFileProperties = new JsonFileProperties("runon_cryptocurrency.json");
    private final String apiAddress = TradingConfig.RUNON_API_ADDRESS;

    private String [] paths;

    private String mergeVolumePath;


    public CryptocurrencyRunonApisOut(){

        String dirPath = CryptocurrencyDataPath.getOpenInterestDirPath() + "/";

        String minute5Path = dirPath + "5m";
        String realPath = dirPath + "real";
        String longShortRatioPath =  dirPath + "long_short_ratio";

        String [] dirPaths = {
                minute5Path,
                realPath,
                longShortRatioPath+"/top_positions/5m",
                longShortRatioPath+"/top_accounts/5m",
                longShortRatioPath+"/all/5m"
        };

        List<String> pathList = new ArrayList<>();
        String[] symbols = {
                "BTCUSDT"
        };
        for(String symbol : symbols){
            for(String path : dirPaths){
                pathList.add(TradingDataPath.getRelativePath(path + "/" + symbol));
            }
        }
        this.paths = pathList.toArray(new String[0]);
        mergeVolumePath = TradingDataPath.getRelativePath(CryptocurrencyDataPath.getMergeVolumeDirPath());
    }


    public void openInterestOut(){
        LineOutManager lineOutManager = LineOutManager.getInstance();

        for(String path : paths){

            long lastTime = jsonFileProperties.getLong(path, -1L );
            TimeLineLock timeLineLock = lineOutManager.get(TradingDataPath.getAbsolutePath(path), PathTimeLine.CSV, TimeName.Type.YEAR_1);
            for(;;){

                try{

                    JSONObject param = new JSONObject();
                    param.put("begin_time", lastTime + 1);
                    param.put("count", 5000);

                    param.put("dir_path", path);
                    param.put("zone_id", TradingTimes.UTC_ZONE_ID.toString());
                    param.put("time_name_type", TimeName.Type.YEAR_1.toString());
                    param.put("time_line_type", PathTimeLine.CSV.toString());

                    HttpApiResponse apiResponse = HttpApis.postJson(apiAddress + "/api/time/data/lines", param.toString());
                    if( apiResponse.getResponseCode() != 200){
                        throw new TradingApiException("apiResponse code:" + apiResponse.getResponseCode() +", " + apiResponse.getMessage());
                    }


                    String message = apiResponse.getMessage();

                    JSONObject response = new JSONObject(message);
                    String code = response.getString("code");
                    if(!code.equals("1")){
                        log.error(response.getString("message"));
                        break;
                    }

                    String [] lines=  JsonOrgUtils.getStrings(response.getJSONArray("lines"));

                    if(lines.length == 0){
                        break;
                    }

                    timeLineLock.updateSum(lines, 2000L);
                    long maxTime = TimeLines.getMaxTime(PathTimeLine.CSV, lines);
                    lastTime = maxTime+1;
                    jsonFileProperties.set(path, lastTime);
                }catch (Exception e){
                    log.error(ExceptionUtils.getStackTrace(e));
                    break;
                }
            }
        }
    }

    public void mergeVolumeOut(){
        LineOutManager lineOutManager = LineOutManager.getInstance();
        TimeLineLock timeLineLock = lineOutManager.get(TradingDataPath.getAbsolutePath(mergeVolumePath), PathTimeLine.JSON,  TimeName.Type.DAY_5);

        long lastTime = jsonFileProperties.getLong(mergeVolumePath, -1L );
        for(;;) {
            try {
                JSONObject param = new JSONObject();
                param.put("begin_time", lastTime + 1);
                param.put("count", 5000);

                param.put("dir_path", mergeVolumePath);
                param.put("zone_id", TradingTimes.UTC_ZONE_ID.toString());
                param.put("time_name_type", TimeName.Type.DAY_5.toString());
                param.put("time_line_type", PathTimeLine.JSON.toString());

                HttpApiResponse apiResponse = HttpApis.postJson(apiAddress + "/api/time/data/lines", param.toString());
                if (apiResponse.getResponseCode() != 200) {
                    throw new TradingApiException("apiResponse code:" + apiResponse.getResponseCode() + ", " + apiResponse.getMessage());
                }

                String message = apiResponse.getMessage();

                JSONObject response = new JSONObject(message);
                String code = response.getString("code");
                if(!code.equals("1")){
                    log.error(response.getString("message"));
                    break;
                }

                String [] lines=  JsonOrgUtils.getStrings(response.getJSONArray("lines"));

                if(lines.length == 0){
                    break;
                }

                timeLineLock.updateSum(lines, 2000L);
                long maxTime = TimeLines.getMaxTime(PathTimeLine.JSON, lines);
                lastTime = maxTime+1;
                jsonFileProperties.set(mergeVolumePath, lastTime);

            }catch (Exception e){
                log.error(ExceptionUtils.getStackTrace(e));
                break;
            }
        }

    }

    public void update(){
        openInterestOut();
        mergeVolumeOut();
    }

    public static void main(String[] args) {

        new CryptocurrencyRunonApisOut().update();

    }
}
