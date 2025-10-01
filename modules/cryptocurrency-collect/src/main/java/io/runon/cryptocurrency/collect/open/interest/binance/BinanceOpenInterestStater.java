package io.runon.cryptocurrency.collect.open.interest.binance;

import io.runon.commons.config.Config;

/**
 * 미결제 약정 수집 서비스
 * @author macle
 */
public class BinanceOpenInterestStater {

    public static BinanceOpenInterestCollect start(){
        BinanceOpenInterestCollect binanceOpenInterestCollect = new BinanceOpenInterestCollect();
        BinanceOpenInterestCollectRealService binanceOpenInterestCollectRealService = new BinanceOpenInterestCollectRealService(binanceOpenInterestCollect);
        binanceOpenInterestCollectRealService.start();
        BinanceOpenInterestCollectMinuteService binanceOpenInterestCollectMinuteService = new BinanceOpenInterestCollectMinuteService(binanceOpenInterestCollect);
        binanceOpenInterestCollectMinuteService.start();

        return binanceOpenInterestCollect;
    }

    public static void main(String[] args) {
        Config.getConfig("");
        start();

    }
}