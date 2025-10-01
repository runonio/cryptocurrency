package io.runon.cryptocurrency.collect.fearandgreed;

import io.runon.commons.config.Config;
import io.runon.commons.apis.http.HttpApis;
import io.runon.trading.data.RatingScore;
import io.runon.trading.data.TimeText;
import io.runon.trading.data.TimeTextUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * alternative.me/crypto/fear-and-greed-index/
 * 위 사이트에서 제공하는
 * fear and greed 수집
 * @author macle
 */
public class FearAndGreedAlternativeCollect{

    public static void update(){
        update(100);
    }

    public static void update(int limit){
        String jsonText = collect(limit);
        JSONObject object = new JSONObject(jsonText);
        JSONArray array = object.getJSONArray("data");

        for (int i = 0; i <array.length() ; i++) {
            JSONObject row = array.getJSONObject(i);
            RatingScore ratingScore = new RatingScore();
            ratingScore.setTime(Long.parseLong(row.getString("timestamp")) + 1000L);
            ratingScore.setRating(row.getString("value_classification").toLowerCase());
            ratingScore.setScore(row.getBigDecimal("value"));

            TimeText timeText = new TimeText();
            timeText.setDataKey("cryptocurrency_fear_and_greed_alternative");
            timeText.setTime(ratingScore.getTime());
            timeText.setDataValue(ratingScore.toString());
            timeText.setUpdatedAt(System.currentTimeMillis());
            TimeTextUtils.update(timeText);
        }
    }


    public static String collect(){
        return collect(100);
    }

    public static String collect(int limit){
        return HttpApis.getMessage("https://api.alternative.me/fng/?limit=" + limit);
    }

    public static void main(String[] args) {
        Config.getConfig("");
        System.out.println(collect(2));
    }
}
