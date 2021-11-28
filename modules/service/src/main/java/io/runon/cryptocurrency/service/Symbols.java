package io.runon.cryptocurrency.service;

import com.seomse.commons.utils.ExceptionUtil;
import io.runon.cryptocurrency.service.redis.Redis;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 암호화폐 거래소 심볼 관련 매소드
 * @author macle
 */
@Slf4j
public class Symbols {


    public static String [] getSymbols(String exchangeId, int rank, String [] defaultSymbols, boolean isTradingPrice){
        try {

            Map<String, String> map  = Redis.hgetallAsync(exchangeId);
            String [] dataArray = map.values().toArray(new String[0]);

            TextScore [] textScores = new TextScore[dataArray.length];

            for (int i = 0; i <textScores.length ; i++) {
                String data = dataArray[i];
                JSONObject jsonObject = new JSONObject(data);
                String symbol = jsonObject.getString("symbol");
                double score;

                try {
                    if(isTradingPrice){
                        score = jsonObject.getDouble("trade_price_24h");
                    }else{
                        score = jsonObject.getDouble("close_price") * jsonObject.getDouble("volume_24h");
                    }
                }catch (Exception e){
                    score = 0;
                }
                textScores[i] = new TextScore(symbol, score);
            }

            Arrays.sort(textScores, TextScore.SORT_DESC);

            int length = Math.min(rank, textScores.length);
            Set<String> symbolSet = new HashSet<>();

            for (int i = 0; i <length ; i++) {
                symbolSet.add(textScores[i].getText());
            }

            if(defaultSymbols != null) {
                symbolSet.addAll(Arrays.asList(defaultSymbols));
            }
            return symbolSet.toArray(new String[0]);

        }catch(Exception e){
            log.error(ExceptionUtil.getStackTrace(e));
        }

        return null;
    }
}
