package io.runon.cryptocurrency.collect.starter;

import io.runon.commons.config.Config;
import io.runon.cryptocurrency.collect.fearandgreed.FearAndGreedCollectService;
import io.runon.cryptocurrency.merge.volume.MergeVolumeStarter;
import io.runon.cryptocurrency.collect.open.interest.binance.BinanceOpenInterestCollect;
import io.runon.cryptocurrency.collect.open.interest.binance.BinanceOpenInterestStater;
import lombok.extern.slf4j.Slf4j;

/**
 * 데이터 수집용 서비스
 * @author macle
 */

@Slf4j
public class CryptocurrencyCollectStarter {
    public static void main(String[] args) {
//        new BinanceFuturesCandleCollectService().start();
//        new BinanceSpotCandleCollectService().start();

//        BinanceOpenInterestCollect binanceOpenInterestCollect = new BinanceOpenInterestCollect();
//        BinanceOpenInterestCollectRealService binanceOpenInterestCollectRealService = new BinanceOpenInterestCollectRealService(binanceOpenInterestCollect);
//        binanceOpenInterestCollectRealService.start();
//        BinanceOpenInterestCollectMinuteService binanceOpenInterestCollectMinuteService = new BinanceOpenInterestCollectMinuteService(binanceOpenInterestCollect);
//        binanceOpenInterestCollectMinuteService.start();

        Config.getConfig("");
        MergeVolumeStarter.start();
        BinanceOpenInterestCollect collect = BinanceOpenInterestStater.start();

        new CryptocurrencyDataUploadService(collect).start();
        if(Config.getBoolean("cryptocurrency.collect.fear.and.greed.flag", false)) {
            new FearAndGreedCollectService().start();
        }
    }
}
