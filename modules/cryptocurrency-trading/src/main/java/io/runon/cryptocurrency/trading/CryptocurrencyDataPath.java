package io.runon.cryptocurrency.trading;

import com.seomse.commons.config.Config;

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
        String spotCandleDirPath = Config.getConfig("cryptocurrency.spot.candle.dir.path");
        if (spotCandleDirPath == null) {
            spotCandleDirPath = Config.getConfig("trading.data.path", "runon/data") + "/cryptocurrency/spot/candle";
        }

        return spotCandleDirPath;
    }

    /**
     * 선물 캔들 데이터 경로 얻기
     * @return 선물 캔들 데이터 경로
     */
    public static String getFuturesCandleDirPath() {
        String futuresCandleDirPath = Config.getConfig("cryptocurrency.futures.candle.dir.path");
        if (futuresCandleDirPath == null) {
            futuresCandleDirPath = Config.getConfig("trading.data.path", "runon/data") + "/cryptocurrency/futures/candle";
        }

        return futuresCandleDirPath;
    }

    /**
     * 현물 호가창 데이터 경로 얻기
     * @return 현물 호가창 데이터 경로
     */
    public static String getSpotOrderBookDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.spot.order.book.dir.path");
        if (dirPath == null) {
            dirPath = Config.getConfig("trading.data.path", "runon/data") + "/cryptocurrency/spot/order_book";
        }

        return dirPath;
    }

    /**
     * 선물 호가창 데이터 경로 얻기
     * @return 선물 호가창 데이터 경로
     */
    public static String getFuturesOrderBookDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.futures.order.book.dir.path");
        if (dirPath == null) {
            dirPath = Config.getConfig("trading.data.path", "runon/data") + "/cryptocurrency/futures/order_book";
        }

        return dirPath;
    }

    /**
     * 미체결 약정 데이터 경로 얻기
     * @return 미체결 약정 데이터 경로
     */
    public static String getOpenInterestDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.open.interest.dir.path");
        if (dirPath == null) {
            dirPath = Config.getConfig("trading.data.path", "runon/data") + "/cryptocurrency/futures/open_interest";
        }

        return dirPath;
    }


    /**
     * 합산 거래량 데이터 경로 얻기
     * @return 합산 거래량 데이터 경로
     */
    public static String getMergeVolumeDirPath() {
        String dirPath = Config.getConfig("cryptocurrency.merge.volume.dir.path");
        if (dirPath == null) {
            dirPath = Config.getConfig("trading.data.path", "runon/data") + "/cryptocurrency/merge/volume";
        }

        return dirPath;
    }

    public static void main(String[] args) {
        System.out.println(getOpenInterestDirPath());
    }

}
