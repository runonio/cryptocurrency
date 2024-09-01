package io.runon.cryptocurrency.trading;

import com.seomse.commons.config.Config;
import io.runon.trading.TradingConfig;
import io.runon.trading.data.TradingDataPath;

import java.nio.file.FileSystems;

/**
 * 암호화폐 데이터 경로
 * @author macle
 */
public class CryptocurrencyDataPath {

    /**
     * 현물 캔들 데이터 경로 얻기
     * @return 현물 캔들 데이터 경로
     */
    public static String getSpotCandleDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.spot.candle.dir.path");
        if (dirPath == null) {
            String fileSeparator = FileSystems.getDefault().getSeparator();
            dirPath = TradingConfig.getTradingDataPath() + "/cryptocurrency/spot/candle";
        }

        dirPath = TradingDataPath.getAbsolutePath(dirPath);

        return dirPath;
    }

    /**
     * 선물 캔들 데이터 경로 얻기
     * @return 선물 캔들 데이터 경로
     */
    public static String getFuturesCandleDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.futures.candle.dir.path");
        if (dirPath == null) {
            dirPath = TradingConfig.getTradingDataPath() + "/cryptocurrency/futures/candle";
        }

        dirPath = TradingDataPath.getAbsolutePath(dirPath);

        return dirPath;
    }

    /**
     * 현물 호가창 데이터 경로 얻기
     * @return 현물 호가창 데이터 경로
     */
    public static String getSpotOrderBookDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.spot.order.book.dir.path");
        if (dirPath == null) {
            dirPath = TradingConfig.getTradingDataPath() + "/cryptocurrency/spot/order_book";
        }
        dirPath = TradingDataPath.getAbsolutePath(dirPath);
        return dirPath;
    }

    /**
     * 선물 호가창 데이터 경로 얻기
     * @return 선물 호가창 데이터 경로
     */
    public static String getFuturesOrderBookDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.futures.order.book.dir.path");
        if (dirPath == null) {
            dirPath = TradingConfig.getTradingDataPath() + "/cryptocurrency/futures/order_book";
        }
        dirPath = TradingDataPath.getAbsolutePath(dirPath);
        return dirPath;
    }

    /**
     * 미체결 약정 데이터 경로 얻기
     * @return 미체결 약정 데이터 경로
     */
    public static String getOpenInterestDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.open.interest.dir.path");
        if (dirPath == null) {
            dirPath = TradingConfig.getTradingDataPath() + "/cryptocurrency/futures/open_interest";
        }
        dirPath = TradingDataPath.getAbsolutePath(dirPath);
        return dirPath;
    }


    /**
     * 합산 거래량 데이터 경로 얻기
     * @return 합산 거래량 데이터 경로
     */
    public static String getMergeVolumeDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.merge.volume.dir.path");
        if (dirPath == null) {
            dirPath = TradingConfig.getTradingDataPath() + "/cryptocurrency/merge/volume";
        }
        dirPath = TradingDataPath.getAbsolutePath(dirPath);
        return dirPath;
    }

    public static void main(String[] args) {

        System.out.println(getFuturesCandleDirPath());
        System.out.println(getOpenInterestDirPath());
    }

}
