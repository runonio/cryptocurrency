import io.runon.commons.utils.time.DateUtil;
import io.runon.commons.utils.time.Times;
import io.runon.trading.TradingTimes;

import java.time.Instant;
import java.time.ZonedDateTime;

public class TimeText {
    public static void main(String[] args) {

        long time = 1660869300000L;

        Instant i = Instant.ofEpochMilli(time);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(i, TradingTimes.UTC_ZONE_ID);
        System.out.println(zonedDateTime.getYear() +  DateUtil.getDateText(zonedDateTime.getMonthValue()));
        System.out.println(Times.ymdhm(time, TradingTimes.UTC_ZONE_ID ));
    }
}
