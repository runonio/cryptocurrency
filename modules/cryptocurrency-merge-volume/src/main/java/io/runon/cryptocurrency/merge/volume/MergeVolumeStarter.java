package io.runon.cryptocurrency.merge.volume;

import io.runon.commons.config.Config;
import io.runon.commons.utils.time.Times;
import io.runon.commons.data.service.collect.CollectErrorMonitoringService;
import io.runon.cryptocurrency.merge.volume.exchanges.binance.*;
import io.runon.cryptocurrency.trading.service.DataStreamKeepAliveService;
import lombok.extern.slf4j.Slf4j;

/**
 * candle merge starter
 * 과거 기록을 위한 1분봉 저장
 * 5초마다 가격 거래량 거래대금 ( 체결강도 거래량 거래대금 저장)
 *
 * @author macle
 */
@Slf4j
public class MergeVolumeStarter {

    public static void start(){
        try {
            //30분까지만 지원
            long[] secondTimes = {10000L, 20000L, 30000L, Times.MINUTE_1, Times.MINUTE_3, Times.MINUTE_5, Times.MINUTE_1 * 7, Times.MINUTE_10, Times.MINUTE_15, Times.MINUTE_30};

            MergeVolume mergeVolume = new MergeVolume();
            mergeVolume.setSecondTimes(secondTimes);
            mergeVolume.load();


            //바이낸스 수신이 시작한후에 다른항목이 시작 ( 가격정보는 바이낸스 가격만 활용)
            //테더가 깨지는 이슈로 인해서 가격정보를 busd로 변경
            new BinanceBtcCandleStream("binance_btc_busd_merge_candle", mergeVolume).connect();
            new BinanceBtcTradeStream("binance_btc_price", mergeVolume).connect();
            //바이낸스 거래소에서 가격을 가져오고 실행이 되어야함
            Thread.sleep(2000L);
//        binance_futures_btc_usdt_merge_candle

            //바이낸스선물
            new BinanceFuturesBtcCandleStream("binance_futures_btc_usdt_merge_candle", mergeVolume).connect();
            Thread.sleep(2000L);

            new BinanceBtcUsdtCandleStream("binance_btc_usdt_merge_candle", mergeVolume).connect();
            new BinanceFuturesBtcBusdCandleStream("binance_futures_btc_busd_merge_candle", mergeVolume).connect();

            new MergeVolumeAverageService(mergeVolume).start();

            new MergeVolumeService(mergeVolume).start();

            new DataStreamKeepAliveService().start();
            new CollectErrorMonitoringService().start();

            //usdt 거래소 busd로 변경되었으므로 usdt 거래소 추가

            //코인베이스
//        new CoinbaseBtcTradeStream("coinbase_btc_merge_trade", mergeCandles).connect();

            //okx
//        new OkxBtcTradeStream("okx_btc_merge_trade", mergeCandles).connect();
//        OkxFuturesBtcTradeStream okxFuturesBtcTradeStream = new OkxFuturesBtcTradeStream("okx_futures_btc_merge_trade", mergeCandles);
//        okxFuturesBtcTradeStream.connect();
//        new OkxFuturesIdChangeService(okxFuturesBtcTradeStream).start();

            //ftx
//        String [] ftxMessages = {
//                "{\"channel\":\"trades\",\"op\":\"subscribe\",\"market\":\"BTC/USD\"}"
//                , "{\"channel\":\"trades\",\"op\":\"subscribe\",\"market\":\"BTC/USDT\"}"
//        };
//        FtxBtcTradeStream ftxFutureStream = new  FtxBtcTradeStream("ftx_futures_btc_merge_trade", mergeCandles);
//        ftxFutureStream.setSubscribeMessage("{\"channel\":\"trades\",\"op\":\"subscribe\",\"market\":\"BTC-PERP\"}");
//        ftxFutureStream.connect();

//        FtxBtcTradeStream ftxStream = new  FtxBtcTradeStream("ftx_btc_merge_trade", mergeCandles);
//        ftxStream.setSubscribeMessage(ftxMessages);
//        ftxStream.connect();

            //후오비
//        new HuobiBtcTradeStream("houbi_btc_merge_trade", mergeCandles).connect();

            //업비트
//        new UpbitBtcTradesStream("upbit_btc_merge_trade", mergeCandles).connect();

            //빗썸
//        new BithumbBtcTradeStream("bithumb_btc_merge_trade", mergeCandles).connect();

            //kraken
//        new KrakenXbtTradeStream("kraken_xbt_trade", mergeCandles).connect();
//        new KrakenFuturesXbtTradeStream("kraken_futures_xbt_trade", mergeCandles).connect();
        }catch (Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }


    @SuppressWarnings("resource")
    public static void main(String[] args){

        //초기 설정정보가 세팅되게 강제호출
        Config.getConfig("");
        start();

    }


}
